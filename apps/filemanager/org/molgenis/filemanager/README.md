FileManager
----------------------------------------------

FileManager is a simple framework for tracking files accross multiple storage pools:
	- Store all your files using FileLocation + FileServer in the database
	- Link redundant copies of the same file together on FileReference (i.e. a conceptual file)
	- Automatically calculate md5 on the different file locations to verify its existence
	- Automatically check if files exist in the expected location
	- Use the REST api to programmatically interact
	
Discussion: for security we need to use ssh-keys.
