package de.chager.sandbox.securityservice.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * A custom JdbcUserService to handle the custom user object
 * 
 * @author Dipl.-Inf. (FH) Christian Hager
 */
public class JdbcUserDetailsManager
		extends org.springframework.security.provisioning.JdbcUserDetailsManager
{
	private static final String SALT_ENCRYPTION_SECRET = "fksfjk23k23jladjsal";
	private static final String createAuthoritySql = "insert into authorities (authority, users_id) values (?,?)";
	private String createUserSql;
	
	@Override
	public void createUser(
			final UserDetails userDetails )
	{
		final User user = (User) userDetails;
		validateUserDetails( user );
		getJdbcTemplate().update( getCreateUserSql(), new PreparedStatementSetter()
			{
				@Override
				public void setValues(
						final PreparedStatement ps )
						throws SQLException
				{
					ps.setString( 1, user.getUsername() );
					ps.setString( 2, user.getPassword() );
					ps.setBoolean( 3, user.isEnabled() );
					ps.setString( 4, getSaltEncryptor().encrypt( user.getSalt() ) );
				}
			} );
		if (getEnableAuthorities())
		{
			insertUserAuthorities( user );
		}
	}
	
	public String getCreateUserSql()
	{
		return createUserSql;
	}
	
	@Override
	public void setCreateUserSql(
			final String createUserSql )
	{
		this.createUserSql = createUserSql;
	}
	
	@Override
	protected UserDetails createUserDetails(
			final String username,
			final UserDetails userFromUserQuery,
			final List<GrantedAuthority> combinedAuthorities )
	{
		String returnUsername = userFromUserQuery.getUsername();
		if (!isUsernameBasedPrimaryKey())
		{
			returnUsername = username;
		}
		return new User( returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities,
				((User) userFromUserQuery).getSalt() );
	}
	
	@Override
	protected List<UserDetails> loadUsersByUsername(
			final String username )
	{
		return getJdbcTemplate().query( getUsersByUsernameQuery(), new String[] {
			username
		}, new RowMapper<UserDetails>()
			{
				@Override
				public UserDetails mapRow(
						final ResultSet rs,
						final int rowNum )
						throws SQLException
				{
					final String username = rs.getString( 1 );
					final String password = rs.getString( 2 );
					final boolean enabled = rs.getBoolean( 3 );
					final String salt = getSaltEncryptor().decrypt( rs.getString( 4 ) );
					return new User( username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES, salt );
				}
			} );
	}
	
	private StrongTextEncryptor getSaltEncryptor()
	{
		final StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword( SALT_ENCRYPTION_SECRET ); // setting a password to encrypt the salt
		return encryptor;
	}
	
	private void insertUserAuthorities(
			final UserDetails user )
	{
		for (final GrantedAuthority auth : user.getAuthorities())
		{
			getJdbcTemplate().update( createAuthoritySql, user.getUsername(), auth.getAuthority() );
		}
	}
	
	private void validateAuthorities(
			final Collection<GrantedAuthority> authorities )
	{
		Assert.notNull( authorities, "Authorities list must not be null" );
		for (final GrantedAuthority authority : authorities)
		{
			Assert.notNull( authority, "Authorities list contains a null entry" );
			Assert.hasText( authority.getAuthority(), "getAuthority() method must return a non-empty string" );
		}
	}
	
	private void validateUserDetails(
			final UserDetails user )
	{
		Assert.hasText( user.getUsername(), "Username may not be empty or null" );
		validateAuthorities( user.getAuthorities() );
	}
}
