# LTE-UserDevice-
- Requirments:
  - In the command line, write the commands to install java14:
    sudo apt install openjdk-14-jre-headless
    sudo apt install openjdk-14-jdk-headless
- On Linux, the application can be run using the scrpits. To do this, first write next commands in the command lime from directory with file compile.sh:
  - sh ./compile.sh
  - sh ./start.sh 
- On Windows, you can also run the application through a script. To do this, you need to register compile in the console.bat, then start. bat. 
- __On Windows, problems may occur if the JAVA_HOME variable is not registered in the system. In this case, it is necessary in the compile file.bat specify the path to javac.(bold)__
- Default result of running this commands will be opened window with name "Оконечное устройство" with two buttons "Запустить" and "Остановить"
