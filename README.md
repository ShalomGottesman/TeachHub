# TeachHub

An ease-of-use application based on an abstraction of the GitHub REST API designed to assist professors in generating and cloning many git repositories for students. Additional usage allows user to add/remove many collaborators to repositories (specifically TAs and other Professors).


![GitHub Logo](/README_src/TeachHub_openMsg.png)


This guide is broken down into three section: Setup, File Syntax, Execution.

## Setup
1. For this application to run correctly, you should have Java 7 or later installed on you computer. 
2. This application stores .CSV files on your computer for undo/redo purposes. By default, this application will create a folder called "TeachHub" in your home directory and will output a message on startup declaring this. 

![GitHub Logo](/README_src/TeachHub_envrVar.png)

To change this, create an enviorment variable called "TeachHub" (without quotes) with the value being the location you would like the application to create the sub directory "/TeachHub" to store the files.


## File Syntax
Currently, TeachHub can only operate on .CSV files (possible update for excel files coming). That being said, the CSV files must be formatted correctly for the application to work correctly. These are the proper headers that the first line of the CSV must have. It does not have to have all the heads, just the ones applicable.
 * User
 * Repo_Name
 * Create_Repo
 * Make_Private
 * Prof_Add_Collab
 * Prof_Remove_Collab
 * TA_Add_Collab
 * TA_Remove_Collab
 * Student_Add_Collab
 * Student_Remove_Collab
 * Git_Clone_To_Computer?
 * Git_Clone_Location
 * Delete_Repo
 
 Each line under the header line will be parsed into its own command to be executed. Note that each command must be "reletively complete" to each other command in the file. This means that if one line uses a tag, so must all the rest. There cannot be empty slots in the CSV file.
 
 It is recommended that at max one of Create_Repo and Delete_Repo be used per file to be executed, as it has not been tested if the logic will compute fully when trying to deal with both.
 
 Sample CSVs are shown beloew:
 
 Sample 1
 
 ![GitHub Logo](/README_src/TeachHub_CSV-ex1.png)
 
 
 Sample 2
 
 ![GitHub Logo](/README_src/TeachHub_CSV-ex2.png)
 
 
 Sample 3
 
 ![GitHub Logo](/README_src/TeachHub_CSV-ex3.png)
 
 Once you have a file ready to execute, save the file in any location of your computer that you know the path to.
 
 ## Execution
 Once you have called the application to begin, the options will appear, they are all self explanatory in their prupose. 
 When executing a file, the application will first analyze the file passed in and present: a summer of what is about to be executed, and samples of the undo/redo files that will be generated and saved in the "/TeachHub" folder as discussed in the first section.
 Also when executing a file, there will be some user dialoge to complete with the command line:
 1. User credentials for the application to use with the github API
 2. verification to delete a repo (if applicable)
 3. user credentials for cloneing the repo to the local computer (if applicable)
 
 
 
 




