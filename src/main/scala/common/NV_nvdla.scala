package common

import chisel3._

class NvdlaCorePort extends Bundle {
  val dla_core_clock = Input(Clock())
  val dla_csb_clock = Input(Clock())
  val global_clk_ovr_on = Input(Bool())
  val tmc2slcg_disable_clock_gating = Input(Bool())
  val dla_reset_rstn = Input(Bool())
  val direct_reset_ = Input(Bool())
  val test_mode = Input(Bool())
  val dla_intr = Output(Bool())
  val nvdla_pwrbus_ram_c_pd = Input(UInt(32.W))
  val nvdla_pwrbus_ram_ma_pd = Input(UInt(32.W))
  val nvdla_pwrbus_ram_mb_pd = Input(UInt(32.W))
  val nvdla_pwrbus_ram_p_pd = Input(UInt(32.W))
  val nvdla_pwrbus_ram_o_pd = Input(UInt(32.W))
  val nvdla_pwrbus_ram_a_pd = Input(UInt(32.W))
}

class NV_nvdla extends BlackBox {
  val io = IO(new Bundle {
    val core_port_replaced = new NvdlaCorePort
    val csb_port_replaced = Flipped(new CSBPort())
    val nvdla_core2dbb = new NvdlaRamIF(false, 2, "nvdla_core2dbb_")
  })
}
