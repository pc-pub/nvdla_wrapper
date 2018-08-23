package common

import chisel3._

trait name_constructor {
  val name_prefix: String
  val name_suffix: String
  def make_name(mid_name: String): String = name_prefix + mid_name + name_suffix
}

class AXILitePort(val addr_width: Int = 6, val data_dwords: Int = 1,
                  val name_prefix: String = "", val name_suffix: String = "") extends Bundle with name_constructor {
  // write address channel (AW)
  val awaddr  = Output(UInt(addr_width.W)).suggestName(make_name("awaddr"))
  val awprot  = Output(UInt(3.W)).suggestName(make_name("awprot")) // protection type
  val awvalid = Output(Bool()).suggestName(make_name("awvalid"))
  val awready = Input(Bool()).suggestName(make_name("awready"))
  // write data channel (W)
  val wdata   = Output(UInt((32 * data_dwords).W)).suggestName(make_name("wdata"))
  val wstrb   = Output(UInt((4 * data_dwords).W)).suggestName(make_name("wstrb"))
  val wvalid  = Output(Bool()).suggestName(make_name("wvalid"))
  val wready  = Input(Bool()).suggestName(make_name("wready"))
  // write response channel (B)
  val bresp   = Input(UInt(2.W)).suggestName(make_name("bresp"))
  val bvalid  = Input(Bool()).suggestName(make_name("bvalid"))
  val bready  = Output(Bool()).suggestName(make_name("bready"))
  // read address channel (AR)
  val araddr  = Output(UInt(addr_width.W)).suggestName(make_name("araddr"))
  val arprot  = Output(UInt(3.W)).suggestName(make_name("araprot")) // protection type
  val arvalid = Output(Bool()).suggestName(make_name("arvalid"))
  val arready = Input(Bool()).suggestName(make_name("arready"))
  // read data channel (R)
  val rdata   = Input(UInt((32 * data_dwords).W)).suggestName(make_name("rdata"))
  val rresp   = Input(UInt(2.W)).suggestName(make_name("rresp"))
  val rvalid  = Input(Bool()).suggestName(make_name("rvalid"))
  val rready  = Output(Bool()).suggestName(make_name("rready"))
}

class AXIFullPort(val addr_width: Int = 6, val data_dwords: Int = 1, val id_width: Int = 8, val user_width: Int = 0,
                  val name_prefix: String = "", val name_suffix: String = "") extends Bundle with name_constructor {
  // write address channel (AW)
  val awid    = Output(UInt(id_width.W)).suggestName(make_name("awid"))
  val awaddr  = Output(UInt(addr_width.W)).suggestName(make_name("awaddr"))
  val awlen   = Output(UInt(8.W)).suggestName(make_name("awlen"))
  val awsize  = Output(UInt(3.W)).suggestName(make_name("awsize"))
  val awburst = Output(UInt(2.W)).suggestName(make_name("awburst"))
  val awlock  = Output(Bool()).suggestName(make_name("awlock"))
  val awcache = Output(UInt(4.W)).suggestName(make_name("awcache"))
  val awqos   = Output(UInt(4.W)).suggestName(make_name("awqos"))
  val awregion = Output(UInt(4.W)).suggestName(make_name("awgegion"))
  val awuser  = Output(UInt(user_width.W)).suggestName(make_name("awuser"))
  val awprot  = Output(UInt(3.W)).suggestName(make_name("awprot")) // protection type
  val awvalid = Output(Bool()).suggestName(make_name("awvalid"))
  val awready = Input(Bool()).suggestName(make_name("awready"))
  // write data channel (W)
//  val wid     = Output(UInt(id_width.W)).suggestName(make_name("wid"))  // only supported by AXI3
  val wdata   = Output(UInt((32 * data_dwords).W)).suggestName(make_name("wdata"))
  val wstrb   = Output(UInt((4 * data_dwords).W)).suggestName(make_name("wstrb"))
  val wlast   = Output(Bool()).suggestName(make_name("wlast"))
  val wuser   = Output(UInt(user_width.W)).suggestName(make_name("wuser"))
  val wvalid  = Output(Bool()).suggestName(make_name("wvalid"))
  val wready  = Input(Bool()).suggestName(make_name("wready"))
  // write response channel (B)
  val bid     = Input(UInt(id_width.W)).suggestName(make_name("bid"))
  val bresp   = Input(UInt(2.W)).suggestName(make_name("bresp"))
  val buser   = Input(UInt(user_width.W)).suggestName(make_name("buser"))
  val bvalid  = Input(Bool()).suggestName(make_name("bvalid"))
  val bready  = Output(Bool()).suggestName(make_name("bready"))
  // read address channel (AR)
  val arid    = Output(UInt(id_width.W)).suggestName(make_name("arid"))
  val araddr  = Output(UInt(addr_width.W)).suggestName(make_name("araddr"))
  val arlen   = Output(UInt(8.W)).suggestName(make_name("arlen"))
  val arsize  = Output(UInt(3.W)).suggestName(make_name("arsize"))
  val arburst = Output(UInt(2.W)).suggestName(make_name("arburst"))
  val arlock  = Output(Bool()).suggestName(make_name("arlock"))
  val arcache = Output(UInt(4.W)).suggestName(make_name("arcache"))
  val arqos   = Output(UInt(4.W)).suggestName(make_name("arqos"))
  val arregion = Output(UInt(4.W)).suggestName(make_name("argegion"))
  val aruser  = Output(UInt(user_width.W)).suggestName(make_name("aruser"))
  val arprot  = Output(UInt(3.W)).suggestName(make_name("araprot")) // protection type
  val arvalid = Output(Bool()).suggestName(make_name("arvalid"))
  val arready = Input(Bool()).suggestName(make_name("arready"))
  // read data channel (R)
  val rid     = Input(UInt(id_width.W)).suggestName(make_name("rid"))
  val rdata   = Input(UInt((32 * data_dwords).W)).suggestName(make_name("rdata"))
  val rresp   = Input(UInt(2.W)).suggestName(make_name("rresp"))
  val rlast   = Input(Bool()).suggestName(make_name("rlast"))
  val ruser   = Input(UInt(user_width.W)).suggestName(make_name("ruser"))
  val rvalid  = Input(Bool()).suggestName(make_name("rvalid"))
  val rready  = Output(Bool()).suggestName(make_name("rready"))
}
