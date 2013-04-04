    PROJECT: SeqWare
    FILE: README.md
    PROJECT LEAD: Brian O'Connor <briandoconnor@gmail.com>
    UPDATED: 20130212
    HOMEPAGE: http://seqware.github.com/

## Introduction 

This README is just a quick overview of building SeqWare. See our
[project homepage](http://seqware.github.com) for much more documentation.

This is top level of the [SeqWare Project](http://seqware.github.com).  
This contains the 5 major components of the SeqWare project along with
documentation:

* seqware-meta-db
* seqware-webservice
* seqware-portal
* seqware-pipeline
* seqware-queryengine
* seqware-common
* the http://seqware.github.com website and manual
* seqware-ext-testing

The seqware-common sub-project provides a location for common code
and most of the other sub-projects have this as a dependency.

## Prerequisites 

###A Recent Linux Distribution

This pretty much goes without saying but the SeqWare project is targeted at
Linux.  You may be able to compile and use the software on MacOS X but, in all
honesty, we recommend you use a recent Linux distribution such as Debian
(Ubuntu, Linux Mint, etc) or RedHat (RedHat Enterprise, Fedora, etc).  This
software, although written in Java mostly, was never intended to work on
Windows. If you need to use Windows for development or deployment we recommend
you simply use our VirtualBox VM for both activities, see our extensive documentation
on http://seqware.github.com for more information. You can also use this same
approach on MacOS (or even another version of Linux).

###Java

SeqWare requires Oracle JDK 1.6 or greater, we primarily write and test with JDK 1.6.x.
An example of instructions on how to update your Linux installation can be found [here](https://ccp.cloudera.com/display/CDH4DOC/Before+You+Install+CDH4+on+a+Single+Node#BeforeYouInstallCDH4onaSingleNode-InstalltheOracleJavaDevelopmentKit). You will need to use the method appropriate to your distribution to install this.

###PostgreSQL

In addition to Java we also require PostgreSQL to be installed, version 8.4 is what we currently support.
You need to use the method appropriate to your distribution to install this. For example, on a
recent Debian-based system you would do:

    sudo apt-get install postgresql-8.4

We are currently investigating issues with postgres version 9.1.

###Problems with Maven

Sometimes we run into problems when building, strange missing dependency issues
and broken packages. A lot of the time this is an issue with Maven, try
deleting your ~/.m2 directory and running the build process again.

###Prerequisites ON Mac OS

####SeqWare Query Engine

We use [protobuf](http://code.google.com/p/protobuf/) to handle serialization and de-serialization.

On Mac OS, Protobuf requires the following installation steps:

    wget http://protobuf.googlecode.com/files/protobuf-2.4.1.tar.gz
    tar xzf protobuf-2.4.1.tar.gz
    cd protobuf-2.4.1
    ./configure
    make
    make install

## Building 


### Getting the Source Code 

Our source code is available from [GitHub](https://github.com/SeqWare/seqware) or the "Fork me on GitHub" banner at the upper right of our website

To get a copy of of our source code you will first need to install Git (<code>sudo apt-get install git</code> in Ubuntu) and then clone our repository.

<pre title="Cloning the git repository">
<span class="prompt">~$</span> <kbd>git clone git://github.com/SeqWare/seqware.git</kbd>
Cloning into 'seqware'...
remote: Counting objects: 8984, done.
remote: Compressing objects: 100% (2908/2908), done.
remote: Total 8984 (delta 4308), reused 8940 (delta 4265)
Receiving objects: 100% (8984/8984), 33.57 MiB | 392 KiB/s, done.
Resolving deltas: 100% (4308/4308), done.
</pre>


### Building and Automated Testing 

We're moving to Maven for our builds, this is currently how
you do it in the trunk directory:

    mvn clean install

Maven now separates out unit tests and integration tests as follows.

    mvn clean install # (runs unit tests but skips integration tests, HBase for query engine and Jetty for web service by default) 
    mvn clean install -DskipTests # (skips all unit tests and integration tests)
    mvn clean install -DskipITs=false # (runs all unit tests and ntegration tests that only require postgres as a prerequisite)
    mvn clean install -DskipITs=false -P extITs # (runs all unit tests and all integration tests including those that require Condor/Globus/Pegasus)

In the last case, the extended integration tests profile is used to trigger integration tests that run our command line utilities. 
In order to point your command-line tools at the web service brought up by the integration tests, you will need to modify your SeqWare ~/.seqware/settings to include:

    SW_REST_URL=http://localhost:8889/seqware-webservice 
    QE_NAMESPACE=BATMAN
    QE_PERSIST=true
    QE_HBASE_REMOTE_TESTING=false
    QE_HBASE_PROPERTIES=localhost

If you wish to build the whole of SeqWare at once, you will need:

    export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
    mvn clean install -DskipITs=false


You can also build individual components such as the new query engine with: 

    cd seqware-queryengine
    mvn clean install


#### Web Service Integration Testing

the web service integration tests require an external PostgreSQL instance
running with create DB access for the "seqware" user.  You can set this up by
first installing PostgreSQL (see above) and then do the following:

<pre>
sudo -u postgres createlang plpgsql template1
sudo -u postgres psql -c "CREATE USER seqware WITH PASSWORD 'seqware' CREATEDB;"
</pre>

The test process will use this user to create a temporary test database.



#### Query Engine Integration Testing

By default, our integration test suite runs tests against the [hbase-maven-plugin](https://github.com/wibidata/hbase-maven-plugin). You can, however, run the full test suite against a real Hadoop and HBase cluster; for setup, a good start is to follow Cloudera's [quick start guide](https://ccp.cloudera.com/display/CDH4DOC/CDH4+Quick+Start+Guide). You will then need to set the HBase configuration in `seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java` by turning on HBASE_REMOTE_TESTING and completing a family of terms for HBASE\_PROPERTIES. You can also set these in an external ~/.seqware/settings file.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.

    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, recent versions of Debian (including Ubuntu and Linux Mint) have on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>

You can find the original bug report showing that this was done on purpose here: http://bugs.debian.org/cgi-bin/bugreport.cgi?bug=316099

I don't think RedHat-based distributions use this same convention.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.
    
    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, the latest (v. 13) version of Linux Mint has on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>  


## Creating Workflows Using Maven Archetypes

the normal incantation to create new workflows would be:

	mvn archetype:generate
	...
	Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 228: 685
	[INFO] Using property: groupId = net.sf.seqware
	Define value for property 'artifactId': : helloworld
	Define value for property 'version':  1.0-SNAPSHOT: : 1.0
	[INFO] Using property: package = net.sf.seqware
	Confirm properties configuration:
	groupId: net.sf.seqware
	artifactId: helloworld
	version: 1.0
	package: net.sf.seqware
	 Y: : Y

Across our archetypes, this will usually only prompt you for the workflow name and version while auto-populating other properties. If you wish to override these values, the incantation would be of the format:

	mvn archetype:generate -Dpackage=ca.on.oicr.pde -DgroupId=ca.on.oicr.pde
	...
	Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 228: 685
	[INFO] Using property: groupId = ca.on.oicr.pde
	Define value for property 'artifactId': : helloworld2
	Define value for property 'version':  1.0-SNAPSHOT: : 1.0
	[INFO] Using property: package = ca.on.oicr.pde
	Confirm properties configuration:
	groupId: ca.on.oicr.pde
	artifactId: helloworld2
	version: 1.0
	package: ca.on.oicr.pde
	 Y: : Y

## Installing

See http://seqware.github.com/ for detailed installation instructions
including links to a pre-configured virtual machine that can be used for
testing, development, and deployment.


## Copyright

Copyright 2008-2012 Brian D O'Connor, OICR, UNC, and Nimbus Informatics, LLC

## Contributors

Denis Yuen, Joachim Baran, Yong Liang, Morgan Taschuk, Tony DeBat, and Zheng Zha

## License

SeqWare is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SeqWare is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SeqWare.  If not, see <http://www.gnu.org/licenses/>.


