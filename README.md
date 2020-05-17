# TeachHub

An ease-of-use application based on an abstraction of the GitHub REST API designed to assist professors in generating and cloning many git repositories for students. Additional usage allows user to add/remove many collaborators to repositories (specifically TAs and other Professors).


![GitHub Logo](/README_src/TeachHub_openMsg.png)


This guide is broken down into three section: Setup, File Syntax, Execution.

## Setup
1. For this application to run correctly, you should have Java 8 or later installed on you computer. 
2. This application stores .CSV files on your computer for undo/redo purposes. By default, this application will create a folder called "TeachHub" in your home directory and will output a message on startup declaring this. 

![GitHub Logo](/README_src/TeachHub_envrVar.png)

To change this, create an enviorment variable called "TeachHub" (without quotes) with the value being the location you would like the application to create the sub directory "/TeachHub" to store the files.


## File Syntax
Currently, TeachHub can only operate on .CSV files (possible update for excel files coming). That being said, the CSV files must be formatted properly for the application to work correctly. These are the proper headers that the first line of the CSV must have. It does not have to have all the heads, just the ones applicable.
 * Owner
 * Repo_Name
 * Create_Repo
 * Make_Private
 * Prof_Add_Collab
 * Prof_Remove_Collab
 * TA_Add_Collab
 * TA_Remove_Collab
 * Student_Add_Collab
 * Student_Remove_Collab
 * Accept_Invite
 * Read_Only          <- Currently bugged, only invites regularly
 * Git_Clone_To_Computer?
 * Git_Clone_Location
 * Delete_Repo
 
 Each line under the header line will be parsed into its own command to be executed. Note that each command must be "reletively complete" to each other command in the file. This means that if one line uses a tag, so must all the rest. There cannot be empty slots in the CSV file.

 When it comes to defining a repository's "absolute" location, the URL of a repository shows the relevant information. For example, this repository is ShalomGottesman/TeachHub, ShalomGottesman is the owner of the repository, and TeachHub is the repository's name. It could be however that I am the owner, but the repository is under a guthub organization(ie. TeachHubOrg/TeachHub). In this case the organization's name is the owner field in defining the absolute location of the repository, not the person who owns the organization.
 
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
 When executing a file, the application will first analyze the file passed in and present: a summery of what is about to be executed, and samples of the undo/redo files that will be generated and saved in the "/TeachHub" folder as discussed in the first section.
 Also when executing a file, there will be some user dialoge to complete with the command line:
 1. User credentials for the application to use with the github API
 2. Verification to delete a repo (if applicable)
 3. User credentials for cloneing the repo to the local computer (if applicable)
 
 Note that for all undo/redo purposes, the Cloneing information from the execution file is left out, this is to prevent trying to clone again to the same location or accidentaly deleting files from the local computer.
 
 ![GitHub Logo](/README_src/TeachHub_ExecutionEx.png)

 
 ## FAQ
 ### Q: What happens when I add someone to a repository via TeachHub?
 
 A: The username you provided will be added to the lsit of collaborators for that repository, at which an invitation will be sent to the email address associated with the username to accept the invitation. Note that the user who is accepting the invitation must be logged into GitHub on the browser where he is clicking the link from! Otherwise they will receive an 404 error.
 
 ### Q: How do I start TeachHub?
 
 A: With java installed, type the following into the command line: java -jar <Path-to-TeachHub>TeachHub.jar
 
 ### Q: How to export to a csv from excel?
 
 A: This is very important, when dealing with excel to format your table and then exporting out to a csv file you must make sure you export in the correct format. Excel has many options for how to export to a CSV, there is UTF-8, for Mac, ect. just select Comma Deliminated and it should work. If it does not work try exporting the file again and rerun the application. 
 
 ![GitHub Logo](/README_src/TeachHub_ExcelExport.png)
