
package axi2csb

import chisel3.{util, _}
import chisel3.util._
import common.{AXILitePort, CSBPort}

class AXI2CSB extends Module {
  val io = IO(new Bundle {
    val csb = new CSBPort()
    val axi = Flipped(new AXILitePort(18, 1, "s00_axi_"))
  })
  // parameter
  val addr_width = 16
  val data_bytes = 4
  val data_width = 8 * data_bytes

  // register using to store request address and read data.
  val addr_data_reg = Reg(UInt(math.max(data_width, addr_width).W))
  // helper signal
  val txn_send = io.csb.csb2nvdla_ready && io.csb.csb2nvdla_valid
  val wr_resp = io.axi.bready && io.axi.bvalid
  val rd_resp = io.axi.rready && io.axi.rvalid
  val addr_in = Wire(UInt((addr_width+2).W))

  // states machine to control process
  val sIdle :: sWrRcvAddr :: sWrRcvData :: sWrWaitData :: sWrWaitAddr :: sWrSendRegData :: sWrSendRegAddr :: sWrSend :: sWrWaitResp :: sWrResp :: sRdSend :: sRdWaitResp :: sRdResp :: Nil = Enum(13)
  val curr_state = RegInit(sIdle)
  val next_state = Wire(UInt())
  // states transfer
  curr_state := next_state
  next_state := sIdle
  switch (curr_state) {
    is (sIdle) {
      next_state := curr_state
      when (io.csb.csb2nvdla_ready) {
        when (io.axi.awvalid && io.axi.wvalid) {
          next_state := sWrSend
        }.elsewhen(io.axi.awvalid) {
          next_state := sWrRcvAddr
        }.elsewhen(io.axi.wvalid) {
          next_state := sWrRcvData
        }.elsewhen(io.axi.arvalid) {
          next_state := sRdSend
        }
      }
    }
    is (sWrRcvAddr) {
      next_state := sWrWaitData
    }
    is (sWrRcvData) {
      next_state := sWrWaitAddr
    }
    is (sWrWaitData) {
      next_state := curr_state
      when (io.csb.csb2nvdla_ready && io.axi.wvalid) {
        next_state := sWrSendRegAddr
      }
    }
    is (sWrWaitAddr) {
      next_state := curr_state
      when (io.csb.csb2nvdla_ready && io.axi.awvalid) {
        next_state := sWrSendRegData
      }
    }
    is (sWrSendRegAddr) {
      next_state := curr_state
      when (txn_send) {
        next_state := sWrWaitResp
      }
    }
    is (sWrSendRegData) {
      next_state := curr_state
      when (txn_send) {
        next_state := sWrWaitResp
      }
    }
    is (sWrSend) {
      next_state := curr_state
      when (txn_send) {
        next_state := sWrWaitResp
      }
    }
    is (sWrWaitResp) {
      next_state := curr_state
      when (io.csb.nvdla2csb_wr_complete) {
        next_state := sWrResp
      }
    }
    is (sWrResp) {
      next_state := curr_state
      when (wr_resp) {
        next_state := sIdle
      }
    }
    is (sRdSend) {
      next_state := curr_state
      when (txn_send) {
        next_state := sRdWaitResp
      }
    }
    is (sRdWaitResp) {
      next_state := curr_state
      when (io.csb.nvdla2csb_valid) {
        next_state := sRdResp
      }
    }
    is (sRdResp) {
      next_state := curr_state
      when (rd_resp) {
        next_state := sIdle
      }
    }
  }
  // output
  //io.csb.csb2nvdla_valid := ((sWrSend === curr_state) || (sWrSendRegData === curr_state) ||
  //                          (sWrSendRegAddr === curr_state) || (sRdSend === curr_state))
  io.csb.csb2nvdla_valid := false.B
  addr_in := 0.U
  io.csb.csb2nvdla_addr := addr_in >> 2
  io.csb.csb2nvdla_wdat := 0.U
  io.csb.csb2nvdla_write := false.B
  io.csb.csb2nvdla_nposted := true.B
  // write address channel (AW)
  io.axi.awready := false.B
  // write data channel (W)
  io.axi.wready  := false.B
  // write response channel (B)
  io.axi.bresp   := 0.U
  io.axi.bvalid  := false.B
  // read address channel (AR)
  io.axi.arready := false.B
  // read data channel (R)
  io.axi.rdata   := 0.U
  io.axi.rresp   := 0.U
  io.axi.rvalid  := false.B
  switch (curr_state) {
    is (sIdle) {}
    is (sWrRcvAddr) {
      io.axi.awready := true.B
    }
    is (sWrRcvData) {
      io.axi.wready := true.B
    }
    is (sWrWaitData) {}
    is (sWrWaitAddr) {}
    is (sWrSendRegAddr) {
      io.csb.csb2nvdla_valid := true.B
      addr_in := addr_data_reg(addr_width-1, 0)
      io.csb.csb2nvdla_wdat := io.axi.wdata
      io.csb.csb2nvdla_write := true.B
      io.axi.wready  := true.B
    }
    is (sWrSendRegData) {
      io.csb.csb2nvdla_valid := true.B
      addr_in := io.axi.awaddr
      io.csb.csb2nvdla_wdat := addr_data_reg
      io.csb.csb2nvdla_write := true.B
      io.axi.awready := true.B
    }
    is (sWrSend) {
      io.csb.csb2nvdla_valid := true.B
      addr_in := io.axi.awaddr
      io.csb.csb2nvdla_wdat := io.axi.wdata
      io.csb.csb2nvdla_write := true.B
      io.axi.awready := true.B
      io.axi.wready  := true.B
    }
    is (sWrWaitResp) {

    }
    is (sWrResp) {
      io.axi.bresp   := 0.U
      io.axi.bvalid  := true.B
    }
    is (sRdSend) {
      io.csb.csb2nvdla_valid := true.B
      addr_in := io.axi.araddr
      io.csb.csb2nvdla_write := false.B
      io.axi.arready := true.B
    }
    is (sRdWaitResp) {

    }
    is (sRdResp) {
      io.axi.rdata   := addr_data_reg
      io.axi.rresp   := 0.U
      io.axi.rvalid  := true.B
    }
  }
  // register input
  switch (next_state) {
    is (sWrRcvAddr) {
      addr_data_reg := Cat(0.U, io.axi.awaddr)
    }
    is (sWrRcvData) {
      addr_data_reg := io.axi.wdata
    }
    is (sRdResp) {
      addr_data_reg := io.csb.nvdla2csb_data
    }
  }

  //debug
//  printf(p"Current state = $curr_state, next state = $next_state\n")
}

object GenAXI2CSB extends App {
  chisel3.Driver.execute(args, () => new AXI2CSB)
}
