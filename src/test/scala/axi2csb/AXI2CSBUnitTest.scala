// See README.md for license details.

package axi2csb

import chisel3.{Module, iotesters, printf}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import common._

class AXI2CSBUnitTester(c: AXI2CSBUTWarpper) extends PeekPokeTester(c) {
  def axi_read(port: AXILitePort, addr: Int): BigInt = {
    poke(port.araddr, addr)
    poke(port.arvalid, true)
    poke(port.arprot, 0)
    while (peek(port.arready) == 0) {
      step(1)
    }
    step(1)
    poke(port.arvalid, false)

    poke(port.rready, true)
    while(peek(port.rvalid) == 0) {
      step(1)
    }
    val res = peek(port.rdata)
    step(1)
    poke(port.rready, false)
    step(1)
    res
  }

  def axi_write(port: AXILitePort, addr: Int, data: BigInt): BigInt = {
    poke(port.awaddr, addr)
    poke(port.awvalid, true)
    poke(port.awprot, 0)
    while(peek(port.awready) == 0) {
      step(1)
    }
    step(1)
    poke(port.awvalid, false)

    poke(port.wdata, data)
    poke(port.wvalid, true)
    poke(port.wstrb, 0)
    while(peek(port.wready) == 0) {
      step(1)
    }
    step(1)
    poke(port.wvalid, false)

    poke(port.bready, true)
    while(peek(port.bvalid) == 0) {
      step(1)
    }
    val resp = peek(port.bresp)
    step(1)
    poke(port.bready, false)
    step(1)
    resp
  }

  def init_port(port: AXILitePort) = {
    // init
    poke(port.awvalid, false)
    poke(port.wvalid, false)
    poke(port.bready, false)
    poke(port.arvalid, false)
    poke(port.rready, false)
    step(1)
  }

  private val ad = c
  private val csb_port = ad.io.axi
  private val reg_num = 1 << (c.addr_width - 2)

  init_port(csb_port)
  for(addr <- 0 until reg_num) {
    axi_write(csb_port, addr << 2, addr * 0x1010)
  }
  for(addr <- 0 until reg_num) {
    val rd = axi_read(csb_port, addr << 2)
    val ep = addr * 0x1010
    expect(ep == rd, s"Error when read reg at ${addr}, expect ${ep}, got ${rd}.")
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly example.test.GCDTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly example.test.GCDTester'
  * }}}
  */
class AXI2CSBTester extends ChiselFlatSpec {
  // Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  private val backendNames = if(false && firrtl.FileUtils.isCommandAvailable(Seq("verilator", "--version"))) {
    Array("firrtl", "verilator")
  }
  else {
    Array("firrtl")
  }
  for ( backendName <- backendNames ) {
    "AXI2CSB" should s"calculate proper greatest common denominator (with $backendName)" in {
      Driver(() => new AXI2CSBUTWarpper, backendName) {
        c => new AXI2CSBUnitTester(c)
      } should be (true)
    }
  }

  "Basic test using Driver.execute" should "be used as an alternative way to run specification" in {
    iotesters.Driver.execute(Array(), () => new AXI2CSBUTWarpper) {
      c => new AXI2CSBUnitTester(c)
    } should be (true)
  }

  "using --backend-name verilator" should "be an alternative way to run using verilator" in {
    if(backendNames.contains("verilator")) {
      iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new AXI2CSBUTWarpper) {
        c => new AXI2CSBUnitTester(c)
      } should be(true)
    }
  }

  "running with --is-verbose" should "show more about what's going on in your tester" in {
    iotesters.Driver.execute(Array("--is-verbose"), () => new AXI2CSBUTWarpper) {
      c => new AXI2CSBUnitTester(c)
    } should be(true)
  }

  "running with --fint-write-vcd" should "create a vcd file from your test" in {
    iotesters.Driver.execute(Array("--fint-write-vcd"), () => new AXI2CSBUTWarpper) {
      c => new AXI2CSBUnitTester(c)
    } should be(true)
  }
}
