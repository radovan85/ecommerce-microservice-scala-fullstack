package com.radovan.spring.exceptions

import javax.management.RuntimeErrorException

@SerialVersionUID(1L)
class InstanceUndefinedException(val e: Error) extends RuntimeErrorException(e) {

}