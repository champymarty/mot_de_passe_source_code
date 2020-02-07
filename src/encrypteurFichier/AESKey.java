/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESKey {
	
	private SecretKey secretKey;
	private IvParameterSpec ivParameterSpec;
	
	public AESKey(SecretKey secretKey, IvParameterSpec ivParameterSpec) {
		this.secretKey = secretKey;
		this.ivParameterSpec = ivParameterSpec;
	}
	
	public IvParameterSpec getIvParameterSpec() {
		return ivParameterSpec;
	}
	public SecretKey getSecretKey() {
		return secretKey;
	}

}
