package com.radovan.spring.exceptions

import javax.management.RuntimeErrorException

@SerialVersionUID(1L)
class OutOfStockException(val e: Error) extends RuntimeErrorException(e) {

}
