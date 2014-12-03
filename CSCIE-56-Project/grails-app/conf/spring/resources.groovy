import org.apache.shiro.authc.credential.HashedCredentialsMatcher 

beans = {
	credentialMatcher(HashedCredentialsMatcher ) {
		hashAlgorithmName = 'SHA-512'
		storedCredentialsHexEncoded = true
	}
}