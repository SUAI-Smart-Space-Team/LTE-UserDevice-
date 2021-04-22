# LTE-UserDevice-
- Requirments:
  - For correct work you need to install JDK 14 or higher. To install it on linux follow next commands:
  - In the command line, write the commands to install java14:
    sudo apt install openjdk-14-jre-headless
    sudo apt install openjdk-14-jdk-headless
- On Linux, the application can be run using the scrpits. To do this, first write next commands in the command lime from directory with file compile.sh:
  - sh ./compile.sh
  - sh ./start.sh 
- On Windows, you can also run the application through a script. To do this, you need to register compile in the console.bat, then start.bat
- Default result of running this commands will be opened window with name "Messenger" on top. And two buttons "Turn On" and "Turn Off". After you press button "Turn On" in the center part of the window will be written names of interfaces which is work on your PC. On the right side of screen should be printed service info, which contains network interface's IP and number of port.
- Troubleshoot 
  - __On Windows, problems may occur if the JAVA_HOME variable is not registered in the system. In this case, it is necessary in the compile file.bat specify the path to javac.__
