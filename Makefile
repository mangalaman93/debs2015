SDIR = src
ODIR = bin
JAR_PKG = debs2015.jar
ENTRY_POINT = debs2015

JFLAGS = -cp $(ODIR)/ -d $(ODIR)
JC = javac

.SUFFIXES: .java .class
_OBJ = Area.class Geo.class Route.class Mc.class TenMax.class \
TenMaxProfitability.class TenMaxFrequency.class debs2015.class
OBJ = $(patsubst %,$(SDIR)/%,$(_OBJ))

all: dir $(OBJ) jar

dir:
	mkdir -p $(ODIR)

%.class:
	$(JC) $(JFLAGS) $*.java

jar:
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C bin .

clean:
	rm -rf $(ODIR) *~ *.jar

rebuild: clean all

.PHONY: clean
