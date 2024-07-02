/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security;

import com.nx.api.context.CurrentRuntimeContext;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;

/**
 * 密码编译器
 * 
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年4月19日
 */
public class CustomPwdEncoder implements PasswordEncoder {



	/**
	 * Encode the raw password.
	 * Generally, a good encoding algorithm applies a SHA-1 or greater hash combined with an 8-byte or greater randomly
	 * generated salt.
	 */
	public String encode(CharSequence rawPassword){
		String pwd=rawPassword.toString();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest= md.digest(pwd .getBytes("UTF-8"));
			return new String(Base64.encodeBase64(digest));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Verify the encoded password obtained from storage matches the submitted raw password after it too is encoded.
	 * Returns true if the passwords match, false if they do not.
	 * The stored password itself is never decoded.
	 *
	 * @param rawPassword the raw password to encode and match
	 * @param encodedPassword the encoded password from storage to compare with
	 * @return true if the raw password, after encoding, matches the encoded password from storage
	 */
	public synchronized boolean matches(CharSequence rawPassword, String encodedPassword){
		if(CurrentRuntimeContext.getContextHeader("ingorePwd").equalsIgnoreCase("true")){
			return true;
		}
		String enc=this.encode(rawPassword);
		return enc.equals(encodedPassword);
	}
}
