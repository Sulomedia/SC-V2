Type=Service
Version=7.01
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: true
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Private cb As CacheCleaner
	Dim t2,t3 As Timer
	Private name,apath,l,Types(1),packName As String
	Dim app() As Object
	Dim counter As Int
	Private cts As CustomToast
	Dim piclist As List
	Dim obj As List
	Dim date,time As String
	Dim dir As String=File.DirInternal&"/Bdata"
	Private kvst,kvsdata,alist,dbase,abase,qbase As KeyValueStore
	Private apli As List
	Private pack As PackageManager
	Private package As String="sclean2.com"
End Sub

Sub Service_Create
	'This is the program entry point.
	'This is a good place to load resources that are not specific to a single activity.
	
	DateTime.TimeFormat="HH:mm"
	DateTime.DateFormat="dd.MM.yyy"
	date=DateTime.Date(DateTime.Now)
	time=DateTime.Time(DateTime.Now)
	kvst.Initialize(File.DirInternal,"data_time")
	kvsdata.Initialize(File.DirInternal,"data_data")
	alist.Initialize(File.DirInternal,"adata_data")
	dbase.Initialize(File.DirInternal,"dbase_data")
	abase.Initialize(File.DirInternal,"abase_data")
	qbase.Initialize(File.DirInternal,"qbase_data")
	
	piclist.Initialize
	obj.Initialize
	cb.initialize("cb")
	cts.Initialize
	apli.Initialize
	apli=pack.GetInstalledPackages
	counter=0
	t2.Initialize("t2",1000)
	t3.Initialize("t3",1000)
	t3.Enabled=False
	If Not(File.IsDirectory(File.DirInternal,"Bdata")) Then
		File.MakeDir(File.DirInternal,"Bdata/temp")
		File.WriteList(dir,"clist.txt",obj)
	End If
	info_remote
End Sub

Sub Service_Start (StartingIntent As Intent)
	
End Sub

Sub Service_TaskRemoved
	'This event will be raised when the user removes the app from the recent apps list.
End Sub

'Return true to allow the OS default exceptions handler to handle the uncaught exception.
Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy
	t2.Enabled=False
End Sub

Sub start
	cb.ScanCache
	'kvsdata.DeleteAll
End Sub

Sub info_remote
	apli=pack.GetInstalledPackages
	kvst.Put("ta",apli.Size)
	qbase.DeleteAll
	For i = 0 To apli.Size-1
		qbase.Put(i,apli.Get(i))
	Next
	Log("added: "&kvst.Get("ta"))
End Sub


Sub cb_OnScanStarted
	Log("Started")
	
End Sub


Sub cb_onScanProgress(Current As Int , Total As Int)
	kvsdata.Put("to",Total)
	kvsdata.Put("c",Current)
	CallSub(Main,"update_modul")
End Sub

Sub cb_onScanCompleted(AppsList As Object)
	Dim totalsize As Long = 0
	Dim pm As PackageManager
	Private icon As BitmapDrawable
	piclist.Clear
	obj.Clear
	alist.DeleteAll
	'kvsdata.DeleteAll
	Try
		Dim lu As List = AppsList
		For n = 0 To lu.Size-1
			app= lu.Get(n)
			If app(1) = "com.android.systemui"  Then  Continue 'com.android.systemui This Pakage Have No Icon In Some Android 5
			icon = pm.GetApplicationIcon(app(1))
			totalsize = totalsize+app(2)
			alist.Put(app(1),totalsize+app(2))
			alist.Remove(package)
			obj.Add(app(1))
			kvsdata.PutBitmap(n,icon.Bitmap)
			qbase.Remove("com.android.systemui")
		Next
	Catch
		Log(LastException.Message)
	End Try
	If lu.size >= 0 Then
		Log("T-Size: "&FormatFileSize(totalsize))
		kvsdata.Put("cp",totalsize)
		'CallSub(Main,"c_start")
		'start_c
	Else
		Log("nothing to show")
		CallSub(Main,"finish_modul")
	
	End If
End Sub

Sub start_c
	cb.CleanCache
	CallSub(Main,"c_start")
	Log("start_c:")
End Sub
Sub cb_onCleanStarted
	
	Log("Cleaning..")
End Sub

Sub cb_onCleanCompleted(CacheSize As Long)
	Log(CacheSize&" cleaned")
	kvst.DeleteAll
	kvsdata.Put("cz",FormatFileSize(CacheSize))
	
	'CallSub(Main,"c_start")
End Sub

Sub FormatFileSize(Bytes As Float) As String
	Private Unit() As String = Array As String(" Byte", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB")
	If Bytes = 0 Then
		Return "0 Bytes"
	Else
		Private Po, Si As Double
		Private I As Int
		Bytes = Abs(Bytes)
		I = Floor(Logarithm(Bytes, 1024))
		Po = Power(1024, I)
		Si = Bytes / Po
		Return NumberFormat(Si, 1, 2) & Unit(I)
	End If
End Sub
