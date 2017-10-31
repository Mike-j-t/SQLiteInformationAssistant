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

Viewing Table Data

Long Clicking a Table either in the Database and Tables view or in the Tables and Columns view :-

- Invokes an activity that displays the actual data in the **long-clicked** Table.
- This displays a grid of the data, with vertical rows of data and rows horizontally.
- The SQLite TYPE for each cell is indicated by it's colour according to the legend displayed above the data grid.
- The grid can be scrolled both vertically and hoizontally (by rows and columns).
- Up to 15 columns can be displayed, each column has a heading that shows the column's name.
- Data is shown according to the retrieval via the type's preferred retrieval method i.e.
  - **TEXT** (String) types are retrieved using the `getString` method.
  - **INTEGER** types are retrieved using the `getLong` method.
  - **REAL**/**NUMERIC** types are retrieved using the `getDouble` method.
  - **BLOB** types are retrieved using the `getBlob` method, but are then converted to a hexadecimal representation of the first 24 bytes, so will display up to 48 characeters (24 by default, see setBytesToShowInBlob method to change).
  - If the type cannot be ascertained then it will be shown as an Unknown type.
  -


### Example Screens (default colours)

#### 1. Initial Display with:-

Databases listed in the LeftView, Tables, initially for the first Database, listed in the RightView and Database information (scrolling can be used to traverse the extensive information available) in the LowerView :- 

![sqliteinformationassistant_initialview_v1](https://user-images.githubusercontent.com/19511980/32215329-ce02b656-be75-11e7-8101-855c3b2302a7.JPG)

*Note! rather than show the tables in an empty database, the second database was selected before the screen shot was taken. P.S. an empty database will have the **android_metadata** table*

#### 2. Tables and Columns Display

When a Table in the RightView is clicked, the **LeftView** displays the **Tables**0 and the **RightView** displays the **Columns** for the selected table. Additionally, the **LowerView** displays the **Table's detailed information** as per

![sqliteinformationassistant_tablecolumnview_v1](https://user-images.githubusercontent.com/19511980/32215330-ce39835c-be75-11e7-8296-d4882710af31.JPG)

#### 3. Tables and Columns with Column Information in the LowerView

Clicking on a Column will display that's Column's detailed information (clicking a Table will show the Table's), as per

![sqliteinformationassistant_tablecolumnviewwithcolumninfo_v1](https://user-images.githubusercontent.com/19511980/32215328-cdcc0d0e-be75-11e7-8212-c9af50b360bd.JPG)

### Note!

The **DATABASES** button appears in the latter two screenshots (i.e. the Tables and Columns Display), clicking this will return to the Databases and Tables display.

----
#### 4. Table Data View

*Note! These test tables have been especially generated to have varying storage types (even though a column might be defined as a particular type SQLite allows any type of data to be stored in any column row by row [see SQLite Datatypes](https://sqlite.org/datatype3.html)*. Therefore, this is not typical, rather a typical table would have columns with a dominating type (like the first column, the **_id** column which is of type INTEGER.

![sqliteinformationassistant_tabledataviewer_v1](https://user-images.githubusercontent.com/19511980/32215600-83d32a1a-be76-11e7-96d5-0a775492efcd.JPG)



