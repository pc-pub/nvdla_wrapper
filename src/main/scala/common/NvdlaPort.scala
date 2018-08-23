package common

import chisel3._

class NvdlaRamIF(val addr_qword: Boolean = true, val data_dwords: Int = 1,
                 val name_prefix: String = "", val name_suffix: String = "") extends Bundle with name_constructor {
  val addr_width = if (addr_qword) 64 else 32
  val data_bytes = 4 * data_dwords
  val data_width = 8 * data_bytes
  val id_width = 8
  // write address channel (AW)
  val aw_awid    = Output(UInt(id_width.W)).suggestName(make_name("aw_awid"))
  val aw_awaddr  = Output(UInt(addr_width.W)).suggestName(make_name("aw_awaddr"))
  val aw_awlen   = Output(UInt(4.W)).suggestName(make_name("aw_awlen"))
  val aw_awvalid = Output(Bool()).suggestName(make_name("aw_awvalid"))
  val aw_awready = Input(Bool()).suggestName(make_name("aw_awready"))
  // write data channel (W)
  val w_wdata   = Output(UInt(data_width.W)).suggestName(make_name("w_wdata"))
  val w_wstrb   = Output(UInt(data_bytes.W)).suggestName(make_name("w_wstrb"))
  val w_wlast   = Output(Bool()).suggestName(make_name("w_wlast"))
  val w_wvalid  = Output(Bool()).suggestName(make_name("w_wvalid"))
  val w_wready  = Input(Bool()).suggestName(make_name("w_wready"))
  // write response channel (B)
  val b_bid     = Input(UInt(id_width.W)).suggestName(make_name("b_bid"))
  val b_bvalid  = Input(Bool()).suggestName(make_name("b_bvalid"))
  val b_bready  = Output(Bool()).suggestName(make_name("b_bready"))
  // read address channel (AR)
  val ar_arid    = Output(UInt(id_width.W)).suggestName(make_name("ar_arid"))
  val ar_araddr  = Output(UInt(addr_width.W)).suggestName(make_name("ar_araddr"))
  val ar_arlen   = Output(UInt(4.W)).suggestName(make_name("ar_arlen"))
  val ar_arvalid = Output(Bool()).suggestName(make_name("ar_arvalid"))
  val ar_arready = Input(Bool()).suggestName(make_name("ar_arready"))
  // read data channel (R)
  val r_rid     = Input(UInt(id_width.W)).suggestName(make_name("r_rid"))
  val r_rdata   = Input(UInt(data_width.W)).suggestName(make_name("r_rdata"))
  val r_rlast   = Input(Bool()).suggestName(make_name("r_rlast"))
  val r_rvalid  = Input(Bool()).suggestName(make_name("r_rvalid"))
  val r_rready  = Output(Bool()).suggestName(make_name("r_rready"))
}

class CSBPort(val name_prefix: String = "", val name_suffix: String = "") extends Bundle with name_constructor {
  // request channel
  val csb2nvdla_valid       = Output(Bool()).suggestName(make_name("csb2nvdla_valid"))
  val csb2nvdla_ready       = Input(Bool()).suggestName(make_name("csb2nvdla_ready"))
  val csb2nvdla_addr        = Output(UInt(16.W)).suggestName(make_name("csb2nvdla_addr"))
  val csb2nvdla_wdat        = Output(UInt(32.W)).suggestName(make_name("csb2nvdla_wdat"))
  val csb2nvdla_write       = Output(Bool()).suggestName(make_name("csb2nvdla_write"))
  val csb2nvdla_nposted     = Output(Bool()).suggestName(make_name("csb2nvdla_nposted"))
  // read data channel
  val nvdla2csb_valid       = Input(Bool()).suggestName(make_name("nvdla2csb_valid"))
  val nvdla2csb_data        = Input(UInt(32.W)).suggestName(make_name("nvdla2csb_ready"))
  // write response channel
  val nvdla2csb_wr_complete = Input(Bool()).suggestName(make_name("nvdla2csb_wr_complete"))
}
