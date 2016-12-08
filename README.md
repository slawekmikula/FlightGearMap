
This is an Atlas-like application for Flightgear.
This application does not run on its own: you need FlightGear running in your
system.

# Installation

Connect your Android device to the local Wifi network and run the
application. A dialog should appear with the IP of your device.

In your PC, copy files andatlas.xml and/or andatlas-b199d.xml from protocols directory to the directory $FG_ROOT/Protocol

Run FlightGear with this option:

```bash
--generic=socket,out,5,the-ip-of-your-android-device,5501,udp,andatlas
```
where the-ip-of-your-android-device is the IP shown by your Android device.

If you find any problem, close the android application and run again.
Usually, you do not need to restart FlightGear.

More information available on: http://wiki.flightgear.org/FlightGearMap

Enjoy.

# Legal

This application is under the GPLv3 license.
Check: http://www.gnu.org/licenses/gpl-3.0.html

(c) 2012, Juan Vera del Campo, juanvvc@gmail.com
Graphics: (c) 2012, FlightGear.com
Maps: (c) 2012 Google co.
