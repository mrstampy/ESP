https://sourceforge.net/projects/entrainer
http://entrainer.sourceforge.net

burton@users.sourceforge.net

ESP by MrStampy - 1.1 April 11, 2014

- repackaged for github & Maven repos
- migrated from log4j -> slf4j + logback for logging.
- upgraded Mina library

EntrainerESP - 1.0.1 Final June 08, 2013

- added classes to assist with raw signal processing

EntrainerESP - 1.0.0 Final December 26, 2012

- extracted common code to facilitate implementations for other EEG devices
- renamed project to EntrainerESP (EEG Signal Processing)
- added hooks to process raw eeg events from Neurosky devices in future releases

EntrainerThinkGear - 1.0.0 Final April 27, 2012

- removed extraneous System.out.println
- added overview html for JavaDocs

EntrainerThinkGear - 0.9.0 Beta April 22, 2012

- first *working* version! Reverted MINA connector to java.net.Socket
- now allowing control of raw data acquisition
- now throwing exception if connection fails
- implemented cleanup should connection to ThinkGear socket fail
- changed Disruptor wait strategy to Blocking as Yielding was quite CPU-intensive
- added RW locking to event listeners on ThinkGearSocketConnector
- added connection event/listener on MultiConnectionThinkGearSocket
- added additional state query on MultiConnectionThinkGearSocket
- API freeze for release 1.0.0

EntrainerThinkGear - 0.8.0 Beta April 5, 2012

- missing feature: added event listeners on MultiConnectionThinkGearSocket to allow for non-remote event receipt
- disabling broadcasting by default, allowing programmatic and system property control of enable/disable
- allowing programmatic control of sending of messages to Neurosky device.
- expanded LogUtils to warn & error levels
- ThinkGearSocketConnector now throwing exception if it cannot connect to a MultiConnectionThinkGearSocket
- Simulator now using local listeners for comparing & contrasting event processing times - 2 orders of magnitude
faster (a few thousand nanoseconds vs a couple hundred thousand nanoseconds).
- code repackaging/reorg

EntrainerThinkGear - 0.7.0 Beta April 2, 2012

- added current timestamp and nano time creation to AbstractThinkGearEvent to allow downstream programs to compensate
for any network latency.
- turning off sending of messages to Neurosky device by default.
- using nanosecond data in simulator to obtain a more accurate picture of performance.

EntrainerThinkGear - 0.6.0 Beta April 1, 2012

- Focused on performance, introduced Disruptor (https://code.google.com/p/disruptor/w/list) and Javolution collections
(http://javolution.org/).
- Code reorg to optimize performance.
- Improved NeuroskySimulator to log slow message receipt (slower than 4ms), to execute for a fixed number of cycles
and to exit cleanly.
- NeuroskySimulator now sends raw eeg data @ 500Hz and other data @ 1Hz.
- There appears to be a warmup period of approximately 30 seconds where the vast majority of slow message receipts occur,
after which messages are received less than 4 ms after they are sent.
- improved logging.

EntrainerThinkGear - 0.5.0 Beta April 1, 2012

Initial release.  EntrainerThinkGear expands upon the work of Andreas Borg (borg@elevated.to) to create a connection
to a ThinkGear socket.  Using the Java NIO package via Apache MINA, EntrainerThinkGear allows multiple clients to
subscribe to and receive simultaneously the feed from a Neurosky device.

The class net.sourceforge.entrainer.neurosky.simulator.NeuroskySimulator provides an executable which demonstrates
the use and functionality of this library.  To execute, from the command line (there is no gui currently) type:

java -jar EntrainerThinkGear.jar

Simulated data is created and sent to three subscribers.

This work is released under the GPL 3.0 license.  eSense�, ThinkGear�, MDT�, NeuroBoy� and NeuroSky� are trademarks 
of NeuroSky Inc.