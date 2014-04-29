# ESP - An EEG Signal Processing Library

This library provides the ability to connect to the [NeuroSky](http://www.neurosky.com) MindWave ThinkGear connector and the OCZ Nia. As of version 1.3 the core functionality has been abstracted.  The ThinkGear classes are now a separate library as are the Nia classes. It is designed to be fast and efficient, using high performance Java libraries:

* [Disruptor](https://github.com/LMAX-Exchange/disruptor)
* [Javolution](http://javolution.org/)
* [Apache MINA](http://mina.apache.org/)
* [RxJava](https://github.com/Netflix/RxJava/)
 
## Release 2.0, 29-04-14

* Added [RxJava](https://github.com/Netflix/RxJava/) to the project
* Added ability to tune sample buffer size to more closely represent 1 second's worth of data
* Utilizing Disruptor in raw sample buffer
* Sample rate and sample size now mutable
* Sample rates above 1kHz allowed
* Reasonable results sampling into the 50kHz range

## Maven Dependencies
       <dependency>
           <groupId>com.github.mrstampy</groupId>
           <!-- for the ThinkGear implementation -->
           <artifactId>esp-thinkgear</artifactId>
           <version>2.0</version>
       </dependency>

       <dependency>
           <groupId>com.github.mrstampy</groupId>
           <!-- for the Nia implementation -->
           <artifactId>esp-nia</artifactId>
           <version>2.0</version>
       </dependency>
       
       <dependency>
           <groupId>com.github.mrstampy</groupId>
           <!-- for the base classes, if not using the above implementations -->
           <artifactId>esp</artifactId>
           <version>2.0</version>
       </dependency>

Usage of the library is straight-forward. One only needs to instantiate the AbstractMultiConnectionSocket implementation, add a listener, start the socket and deal with the events as they occur.

## Multiple Connections

As implied by the name of the class, the AbstractMultiConnectionSocket is capable of not only processing ThinkGear messages for a single application but also of broadcasting the messages to separate applications, even on separate machines and devices! This functionality is disabled by default. To enable it either set the System property 'broadcast.messages' to true or instantiate the MultiConnectionThinkGearSocket using one of its other constructors:

	MultiConnectionThinkGearSocket socket = new MultiConnectionThinkGearSocket("ThinkGear socket hostname", true);
	...
	socket.start();

	...and then in another application:

ThinkGearSocketConnector connector = new ThinkGearSocketConnector("Hostname running MultiConnectionThinkGearSocket");
		
	connector.addThinkGearEventListener(new ThinkGearEventListenerAdapter() {
		// overriding methods as appropriate
	});
	
	connector.connect();
	
	//after successful connection...
	connector.subscribe(EventType...);

## Raw Signal Processing (as of version 1.3)

In the development of the [ESP-Nia](http://mrstampy.github.com/ESP-Nia/) implementation the base classes for raw signal processing were developed.  Multi connection socket implementations contain a raw data buffer which contains the current second's worth of data (for the OCZ Nia this translates to 3910 data points, for the ThinkGear devices this translates to 512 data points).

Additional classes have been added to assist with raw signal processing. The examples shown will work with the NeuroSky raw output however these classes can be used for any DSP work.

Additional functionality is described in the JavaDocs. This work is released under the GPL 3.0 license. No warranty of any kind is offered.

[ESP](http://mrstampy.github.io/ESP/) Copyright (C) 2013 - 2014 Burton Alexander. 

eSense, ThinkGear, MDT, NeuroBoy and NeuroSky are trademarks of [NeuroSky Inc](http://www.neurosky.com).
