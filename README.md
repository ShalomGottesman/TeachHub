# TeachHub

An ease-of-use application based on an abstraction of the GitHub REST API designed to assist professors in generating and cloning many git repositories for students. Additional usage allows user to add/remove many collaborators to repositories (specifically TAs and other Professors)


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




