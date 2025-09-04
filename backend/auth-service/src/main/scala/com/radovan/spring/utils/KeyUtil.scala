package com.radovan.spring.utils

import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.security.spec.X509EncodedKeySpec

object KeyUtil {

  @throws[Exception]
  def getPublicKeyFromPrivate(privateKey: PrivateKey): PublicKey = {
    val keyFactory = KeyFactory.getInstance("RSA")
    val spec = new X509EncodedKeySpec(privateKey.getEncoded)
    keyFactory.generatePublic(spec)
  }
}
