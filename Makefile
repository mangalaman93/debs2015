SDIR = src
ODIR = bin
TDIR = test
JAR_PKG = debs2015.jar
ENTRY_POINT = debs2015

JFLAGS = -O -d $(ODIR)
JUNITJAR = /usr/share/java/junit4.jar
JC = javac
JAVA = java

.SUFFIXES: .java .class
_OBJ = Constants.class Area.class Geo.class Route.class Mc.class PaddedAtomicLong.class MyQueue.class \
TenMaxProfitability.class TenMaxFrequency.class debs2015.class

OBJ = $(patsubst %,$(SDIR)/%,$(_OBJ))

_TOBJ = GeoTest.java McTest.java TenMaxFrequencyTest.java \
TenMaxProfitabilityTest.java
TOBJ = $(patsubst %,$(TDIR)/%,$(_TOBJ))

all: dir $(OBJ) jar

dir:
	mkdir -p $(ODIR)

%.class:
	$(JC) -cp $(ODIR)/ $(JFLAGS) $*.java

jar:
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C $(ODIR) .

test: all
ifeq (,$(wildcard $(JUNITJAR)))
    $(error run `apt-get install junit4`!)
endif
	for testfile in $(TOBJ); do \
		$(JC) -cp $(ODIR):$(JUNITJAR) $(JFLAGS) $$testfile; \
		cd $(ODIR) && $(JAVA) -cp $(JUNITJAR):./ org.junit.runner.JUnitCore \
		`echo $$testfile | cut -f2 -d"/" | cut -f1 -d"."`; cd ../; \
	done

itest:
	python script/integration_test.py

delay:
	python script/delay.py

clean:
	rm -rf $(ODIR) *~ *.jar

rebuild: clean all

.PHONY: clean test
