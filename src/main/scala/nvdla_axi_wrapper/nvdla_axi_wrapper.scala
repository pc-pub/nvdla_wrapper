package nvdla_axi_wrapper

import chisel3._
import chisel3.util._
import chisel3.experimental._
import common._
import axi2csb._
import axi2dbb._

class NvdlaAXIWrapper extends RawModule {
  val core_port = IO(new NvdlaCorePort)
  val S00_axi = IO(Flipped(new AXILitePort(18, 1, "S00_axi_")))
  val M00_axi = IO(new AXIFullPort(32, 2, 8, 0, "M00_axi"))

  val nvdla = Module(new NV_nvdla())
  val axi2csb = withClockAndReset(core_port.dla_csb_clock, !core_port.dla_reset_rstn)(Module(new AXI2CSB()))
  val axi2dbb = withClockAndReset(core_port.dla_core_clock, !core_port.dla_reset_rstn)(Module(new AXI2DBB()))
  core_port <> nvdla.io.core_port_replaced
  S00_axi <> axi2csb.io.axi
  M00_axi <> axi2dbb.io.axi
  axi2csb.io.csb <> nvdla.io.csb_port_replaced
  axi2dbb.io.dbbif <> nvdla.io.nvdla_core2dbb
}

object GenNvdlaAXIWrapper extends App {
  chisel3.Driver.execute(args, () => new NvdlaAXIWrapper)
}
