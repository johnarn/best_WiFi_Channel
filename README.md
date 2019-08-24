# Best WiFi Channel

This project has been made as an assignment for my university. The goal of the project is to find the best channel for the user's WiFi, so it has less interference from the WiFi of his neighbors. The application was developed in Linux (Ubuntu 18.04) and pre-installed tools were used to run it.

## Installing

1. Install whatever version of Linux you want.

2. You have to install the tools below.

    * iwlist (apt-get install iwlist)
    * sort (apt-get install sort)
    * grep (apt-get install grep)
    * uniq (apt-get install uniq)
    * tshark (apt-get install tshark)

3. Find and install the driver of your WiFi antenna

4. When everything goes well just compile the program ` javac bestWiFiChannel.java `

5. Execute the program ` java bestWiFihannel `

6. See at the output what is the best WiFi channel to broadcast your WiFi.

## Author

* [John Arnokouros](http://github.com/johnarn) - Initial work - johnarn@windowslive.com

## License
```
The MIT License (MIT)

Copyright (c) 2019 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
