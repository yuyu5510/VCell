#JAVABIN=
#  as JAVABIN=/usr/lib/jvm/default-java/bin
#
#  set your installed javafx-sdk directory 
#JAVAFXMODULE=
#  as JAVAFXMODULE=/opt/Java/JavaFX/javafx-sdk-11.0.2/lib
#
#  set proxy server address and port number, if your machine connects
#  to the internet via proxy
#JAVADOCPROXY=
#  JAVADOCPROXY=-J-Dhttp.proxyHost=proxy.csc.titech.ac.jp -J-Dhttp.proxyPort=8080
JAVAFXMODULE=../javafx-sdk-17.0.7/lib
#JAVAFXMODULE=../javafx-sdk-17.0.7/lib

JAVA=$(JAVABIN)java $(JAVAFLAGS)
JAVAC=$(JAVABIN)javac $(JAVACFLAGS)
JAVADOC=$(JAVABIN)javadoc $(JAVADOCFLAGS)
FIND=find
MKDIR=mkdir -p
MAKE=make
RM=rm -rf

MODULEPATH=$(JAVAFXMODULE)
CLASSPATH=bin:lib/*:
CLASSFLAGS=-classpath "bin:$(CLASSPATH)"
JAVACCLASSFLAGS=-classpath "$(CLASSPATH)"

MODULEFLAGS=--module-path $(MODULEPATH) --add-modules javafx.controls,javafx.swing
JAVACFLAGS= -encoding utf8 -d bin -sourcepath src $(MODULEFLAGS) $(JAVACCLASSFLAGS) -Xlint:deprecation -Xdiags:verbose -Xlint:unchecked
JAVAFLAGS = $(MODULEFLAGS) $(CLASSFLAGS):resource -Djdk.gtk.version=2
JAVADOCFLAGS= -html5 -encoding utf-8 -charset utf-8 --frames -package -d javadoc -sourcepath src $(JAVADOCPROXY) -link https://docs.oracle.com/javase/jp/16/docs/api -link https://openjfx.io/javadoc/17 $(MODULEFLAGS) $(CLASSFLAGS)

PARAMETER=$(SERVADDR)

.PHONY: clean javadoc all

.SUFFIXES: .class .java

.java.class:
	echo
	$(MKDIR) bin

.class:
	$(eval CLS := $(subst /,.,$(@:src/%=%)))
#	$(eval R := $(shell $(JAVAC) $@.java 2>&1))
	RES=`$(JAVAC) $@.java 2>&1` ; \
	if [ -z "$$RES" ]; then \
		$(JAVA) $(CLS)  $(PARAMETER); \
	else \
		echo "$$RES"; \
	fi;

all::	Main

Main::
	$(MAKE) src/$@

clean:	
	$(RM) bin javadoc out.tiff out2.tiff

#javadoc::
#	$(FIND) . -name "._*" -exec $(RM) {} \;
#	$(MKDIR) javadoc
#	$(JAVADOC) .
