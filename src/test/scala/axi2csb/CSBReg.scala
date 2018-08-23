
package axi2csb

import chisel3._
import common._

class CSBReg(val reg_num: Int = 8) extends Module {
  val io = IO(new Bundle {
    val csb = Flipped(new CSBPort())
  })

  val reg = RegInit(VecInit(Seq.fill(reg_num){0.U(32.W)}))

  val wr_resp = RegInit(false.B)
  val rd_valid = RegInit(false.B)
  val rd_data = RegInit(0.U(32.W))

  when (io.csb.csb2nvdla_valid && io.csb.csb2nvdla_write && io.csb.csb2nvdla_nposted) {
    wr_resp := true.B
  }.otherwise {
    wr_resp := false.B
  }

  when (io.csb.csb2nvdla_valid && !io.csb.csb2nvdla_write) {
    rd_valid := true.B
  }.otherwise {
    rd_valid := false.B
  }

  when (io.csb.csb2nvdla_valid) {
    when (io.csb.csb2nvdla_write) {
      reg(io.csb.csb2nvdla_addr) := io.csb.csb2nvdla_wdat
    }.otherwise {
      rd_data := reg(io.csb.csb2nvdla_addr)
    }
  }

  io.csb.csb2nvdla_ready := true.B
  io.csb.nvdla2csb_data := rd_data
  io.csb.nvdla2csb_valid := rd_valid
  io.csb.nvdla2csb_wr_complete := wr_resp

}

class AXI2CSBUTWarpper extends Module {
  val addr_width: Int = 6
  val io = IO(new Bundle{
    val axi = Flipped(new AXILitePort(addr_width, 1))
  })

  val regfile = Module(new CSBReg(1 << addr_width)).io
  val dut = Module(new AXI2CSB)
  val dut_io = dut.io

  dut_io.csb <> regfile.csb
  dut_io.axi <> io.axi
}
