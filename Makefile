
OUTDIR ?= outdir

SRC_PATH = src/main
SRC_DIRS = $(shell find $(SRC_PATH) -type d)
SRC_FILES = $(foreach dir, $(SRC_DIRS), $(wildcard $(dir)/*.scala))
TARGET = NvdlaAXIWrapper.v

$(OUTDIR)/$(TARGET) : $(SRC_FILES)
	sbt "runMain nvdla_axi_wrapper.GenNvdlaAXIWrapper -td ${OUTDIR}" && \
	sed -i -e 's/.core_port_replaced_/./' \
	-e 's/.csb_port_replaced_/./' \
	${OUTDIR}/NvdlaAXIWrapper.v

all:$(TARGET)

.PHONY: clean
clean:
	rm -r $(OUTDIR)
