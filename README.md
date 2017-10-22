# SQLiteInformationAssistant

SQLiteInformationAssistant is designed to be a tool to assist in developing Android Apps that utilise SQLite by allowing the Database(s)
in a package to be viewed.

It has been written as a module and has a currently very basic class **SQLiteInformationAssistant** who's prime purpose is to allow the main SQLite assistant activity to be invoked.
 e.g. **`new SQLiteInformationAssistant(this).show();`** will invoke the assistant.
 
The resultant activity has three main display areas, The **LeftView**, the **RightView** and the **LowerView**.

Initiallly the **LeftView** displays a list of the **databases in the package**, The **RightView** displays the **tables** in the first listed database and
the **LowerView** displays **detailed information** extracted from the database (first) e.g. 

- The USER VERSION (commonly known as the Database Version)
- The ENCODING
- The Database's PATH

If the **DONE** button is clicked the activity finishes and returns to the invoking activity.

When a **Database** in the list is **cliked** the LowerView will change to reflect the clicked database.

If a table is clicked then :-

- The list of Tables moves to the LeftView
- The RightView is populated with a list of the Column's in the Table.
- The LowerView will display information specific to the clicked Table.
- A second button will appear that if clicked will revert to the Database/Table lists.

When Tables and Columns are listed:-

- Clicking a **Table** will change the **RightView** to display the **Columns** of the clicked Table and also change the **LowerView** to display the detailed information relevant to the clicked table.
- Clicking a **Column** will change the **LowerView** to display detailed information about the clicked Column.

Not as yet implemented.

Long Clicking a Table in the list will invoke another activity that will display the data in the table.


### Example Screens

#### Initial Display with Databases listed in the LeftView, Tables, initially for the first Database, listed in the RightView and Database information (scroll through the list) in the LowerView :- 

https://user-images.githubusercontent.com/19511980/31858532-c611f862-b745-11e7-93ff-a1cd75b1fc27.JPG
