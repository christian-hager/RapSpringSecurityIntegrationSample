package de.chager.sandbox.securityservice.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import de.chager.sandbox.securityservice.api.SecurityService;

/**
 * The implementation of the OSGi security service
 * 
 * @author Dipl.-Inf. (FH) Christian Hager
 */
public class SecurityServiceImpl
		implements SecurityService, InitializingBean
{
	private static final String GROUP_ADMINISTRATORS = "administrators";
	private static final String INITIAL_PASSWORD_ADMIN = "admin";
	private static final String USERNAME_ADMIN = "admin";
	private JdbcUserDetailsManager userService = null;
	private AuthenticationManager authenticationManager = null;
	private PasswordEncoder passwordEncoder = null;
	
	/**
	 * Adds an authority to a given group.
	 */
	@Override
	public void addGroupAuthority(
			final String groupName,
			final String role )
	{
		final GrantedAuthority authority = new GrantedAuthorityImpl( role );
		userService.addGroupAuthority( groupName, authority );
	}
	
	/**
	 * Adds a user to the given group.
	 */
	@Override
	public void addUserToGroup(
			final String username,
			final String groupName )
	{
		userService.addUserToGroup( username, groupName );
	}
	
	@Override
	public void afterPropertiesSet()
			throws Exception
	{
		Assert.notNull( userService, "userService must be set" );
		Assert.notNull( authenticationManager, "authenticationManager must be set" );
		Assert.notNull( passwordEncoder, "passwordEncoder must be set" );
	}
	
	@Override
	public int authenticate(
			final String username,
			final String password )
	{
		int status = STATUS_LOGIN_FAILED_UNKOWN;
		Authentication aut = new UsernamePasswordAuthenticationToken( username, password );
		try
		{
			aut = authenticationManager.authenticate( aut );
			SecurityContextHolder.getContext().setAuthentication( aut );
			if (aut.isAuthenticated())
			{
				status = STATUS_LOGIN_SUCCESS;
			}
		}
		catch (final DisabledException e)
		{
			status = STATUS_LOGIN_DISABLED;
		}
		catch (final BadCredentialsException e)
		{
			status = STATUS_LOGIN_BAD_CREDENTIALS;
		}
		return status;
	}
	
	/**
	 * Changes the users password
	 */
	@Override
	public void changePassword(
			final String oldPassword,
			final String newPassword )
			throws AuthenticationException
	{
		userService.changePassword( oldPassword, newPassword );
	}
	
	/**
	 * Creates a new group
	 */
	@Override
	public void createGroup(
			final String groupName,
			final Collection<String> roles )
	{
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (final String role : roles)
		{
			authorities.add( new GrantedAuthorityImpl( role ) );
		}
		userService.createGroup( groupName, authorities );
	}
	
	/**
	 * Creates a new user
	 */
	@Override
	public void createUser(
			final String username,
			final String password,
			final String salt,
			final boolean enabled,
			final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked,
			final Collection<String> roles )
	{
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (final String role : roles)
		{
			authorities.add( new GrantedAuthorityImpl( role ) );
		}
		final UserDetails userDetails = new User( username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, salt );
		userService.createUser( userDetails );
	}
	
	/**
	 * Deletes a group
	 */
	@Override
	public void deleteGroup(
			final String groupName )
	{
		userService.deleteGroup( groupName );
	}
	
	/**
	 * Deletes the users authorities
	 */
	@Override
	public void deleteUser(
			final String username )
	{
		userService.deleteUser( username );
	}
	
	/**
	 * Returns all currently configured groups
	 */
	@Override
	public List<String> findAllGroups()
	{
		return userService.findAllGroups();
	}
	
	/**
	 * Gets the groups authorities.
	 */
	@Override
	public List<String> findGroupAuthorities(
			final String groupName )
	{
		final List<GrantedAuthority> authorities = userService.findGroupAuthorities( groupName );
		final List<String> roles = new ArrayList<String>();
		for (final GrantedAuthority authority : authorities)
		{
			roles.add( authority.getAuthority() );
		}
		return roles;
	}
	
	/**
	 * Returns a list of users in the given group
	 */
	@Override
	public List<String> findUsersInGroup(
			final String groupName )
	{
		return userService.findUsersInGroup( groupName );
	}
	
	@Override
	public void initDatabase()
	{
		/*
		 * Creates the admin account if it is not already there
		 */
		if (userService.loadUsersByUsername( USERNAME_ADMIN ).size() == 0)
		{
			final String salt = new BigInteger( 130, new SecureRandom() ).toString( 32 );
			final UserDetails user = new User( USERNAME_ADMIN, passwordEncoder.encodePassword( INITIAL_PASSWORD_ADMIN, salt ), true, true, true, true,
					AuthorityUtils.NO_AUTHORITIES, salt );
			userService.createUser( user );
			final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
			grantedAuthorities.add( new GrantedAuthorityImpl( "RIGHT_READ_MODELOBJECTS" ) );
			userService.createGroup( GROUP_ADMINISTRATORS, grantedAuthorities );
			userService.addUserToGroup( user.getUsername(), GROUP_ADMINISTRATORS );
		}
	}
	
	/**
	 * Removes an authority from a given group.
	 */
	@Override
	public void removeGroupAuthority(
			final String groupName,
			final String role )
	{
		userService.removeGroupAuthority( groupName, new GrantedAuthorityImpl( role ) );
	}
	
	/**
	 * Removes a user from the given group.
	 */
	@Override
	public void removeUserFromGroup(
			final String username,
			final String groupName )
	{
		userService.removeUserFromGroup( username, groupName );
	}
	
	/**
	 * Renames a group.
	 */
	@Override
	public void renameGroup(
			final String oldName,
			final String newName )
	{
		userService.renameGroup( oldName, newName );
	}
	
	public void setAuthenticationManager(
			final AuthenticationManager authenticationManager )
	{
		this.authenticationManager = authenticationManager;
	}
	
	public void setPasswordEncoder(
			final PasswordEncoder passwordEncoder )
	{
		this.passwordEncoder = passwordEncoder;
	}
	
	public void setUserService(
			final JdbcUserDetailsManager userService )
	{
		this.userService = userService;
	}
	
	/**
	 * Updates a user
	 */
	@Override
	public void updateUser(
			final String username,
			final String password,
			final String salt,
			final boolean enabled,
			final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked,
			final Collection<String> roles )
	{
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (final String role : roles)
		{
			authorities.add( new GrantedAuthorityImpl( role ) );
		}
		final UserDetails userDetails = new User( username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, salt );
		userService.updateUser( userDetails );
	}
	
	/**
	 * Determines if a user corresponding to a given username exists.
	 */
	@Override
	public boolean userExists(
			final String username )
	{
		return userService.userExists( username );
	}
}
