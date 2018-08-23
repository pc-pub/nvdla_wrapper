
package axi2dbb

import chisel3._
import chisel3.util._
import chisel3.experimental.RawModule
import common.{AXIFullPort, NvdlaRamIF}

class AXI2DBB(val addr_qword: Boolean = false, data_dwords: Int = 1) extends Module {
  // parameters
  val addr_width = if (addr_qword) 64 else 32
  val data_bytes = 4 * data_dwords

  val io = IO(new Bundle{
    val dbbif = (Flipped(new NvdlaRamIF(addr_qword, data_dwords, "dbbif_")))
    val axi = (new AXIFullPort(addr_width, data_dwords, 8, 0, "m03_axi"))
  })
  // aw channel
  io.dbbif.aw_awid     <> io.axi.awid
  io.dbbif.aw_awaddr   <> io.axi.awaddr
  io.dbbif.aw_awlen    <> io.axi.awlen
  io.dbbif.aw_awready  <> io.axi.awready
  io.dbbif.aw_awvalid  <> io.axi.awvalid
  io.axi.awsize   := log2Ceil(data_bytes).U
  io.axi.awburst  := 1.U  // incremental type
  io.axi.awlock   := false.B
  io.axi.awcache  := 0.U  // device non bufferable
  io.axi.awqos    := 0.U
  io.axi.awregion := 0.U
  io.axi.awuser   := 0.U
  io.axi.awprot   := 0.U
  // w channel
  io.dbbif.w_wready  <> io.axi.wready
  io.dbbif.w_wvalid  <> io.axi.wvalid
  io.dbbif.w_wdata   <> io.axi.wdata
  io.dbbif.w_wlast   <> io.axi.wlast
  io.dbbif.w_wstrb   <> io.axi.wstrb
  io.axi.wuser     := 0.U
  // b channel
  io.dbbif.b_bready  <> io.axi.bready
  io.dbbif.b_bvalid  <> io.axi.bvalid
  io.dbbif.b_bid     <> io.axi.bid
  io.axi.bresp     <> DontCare  // OK
  io.axi.buser     <> DontCare
  // ar channel
  io.dbbif.ar_arready <> io.axi.arready
  io.dbbif.ar_arvalid <> io.axi.arvalid
  io.dbbif.ar_araddr  <> io.axi.araddr
  io.dbbif.ar_arid    <> io.axi.arid
  io.dbbif.ar_arlen   <> io.axi.arlen
  io.axi.arsize    := log2Ceil(data_bytes).U
  io.axi.arburst   := 1.U                        // incremental type
  io.axi.arlock    := false.B
  io.axi.arcache   := 0.U                        // device non bufferable
  io.axi.arqos     := 0.U
  io.axi.arregion  := 0.U
  io.axi.aruser    := 0.U
  io.axi.arprot    := 0.U
  // r channel
  io.dbbif.r_rid     <> io.axi.rid
  io.dbbif.r_rdata   <> io.axi.rdata
  io.dbbif.r_rvalid  <> io.axi.rvalid
  io.dbbif.r_rready  <> io.axi.rready
  io.dbbif.r_rlast   <> io.axi.rlast
  io.axi.rresp     <> DontCare   // OK
  io.axi.ruser     <> DontCare
}

object GenAXI2DBB extends App {
  chisel3.Driver.execute(args, () => new AXI2DBB(false, 2))
}
