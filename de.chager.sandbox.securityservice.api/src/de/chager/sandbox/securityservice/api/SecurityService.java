package de.chager.sandbox.securityservice.api;

import java.util.Collection;
import java.util.List;

/**
 * The security-service.
 * 
 * @author Dipl.-Inf. (FH) Christian Hager
 */
public interface SecurityService
{
	public static final int STATUS_LOGIN_SUCCESS = 0;
	public static final int STATUS_LOGIN_BAD_CREDENTIALS = 1;
	public static final int STATUS_LOGIN_DISABLED = 2;
	public static final int STATUS_LOGIN_FAILED_UNKOWN = 3;
	
	public void addGroupAuthority(
			final String groupName,
			final String role );
	
	public void addUserToGroup(
			final String username,
			final String groupName );
	
	public int authenticate(
			final String username,
			final String password );
	
	public void changePassword(
			final String oldPassword,
			final String newPassword )
			throws AuthenticationException;
	
	public void createGroup(
			final String groupName,
			final Collection<String> roles );
	
	public void createUser(
			final String username,
			final String password,
			final String salt,
			final boolean enabled,
			final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked,
			final Collection<String> roles );
	
	public void deleteGroup(
			final String groupName );
	
	public void deleteUser(
			final String username );
	
	public List<String> findAllGroups();
	
	public List<String> findGroupAuthorities(
			final String groupName );
	
	public List<String> findUsersInGroup(
			final String groupName );
	
	public void initDatabase();
	
	public void removeGroupAuthority(
			final String groupName,
			final String role );
	
	public void removeUserFromGroup(
			final String username,
			final String groupName );
	
	public void renameGroup(
			final String oldName,
			final String newName );
	
	public void updateUser(
			final String username,
			final String password,
			final String salt,
			final boolean enabled,
			final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked,
			final Collection<String> roles );
	
	public boolean userExists(
			final String username );
}
