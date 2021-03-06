package de.chager.sandbox.securityservice.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * An extended user object
 * 
 * @author Dipl.-Inf. (FH) Christian Hager
 */
public class User
		extends org.springframework.security.core.userdetails.User
{
	private static final long serialVersionUID = 2946078502778162735L;
	private final String salt;
	
	public User (
			final String username,
			final String password,
			final boolean enabled,
			final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked,
			final Collection<? extends GrantedAuthority> authorities,
			final String salt)
	{
		super( username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities );
		this.salt = salt;
	}
	
	public String getSalt()
	{
		return salt;
	}
}
