FFmpeg version:




How to Build:
1. Download mingw-w64-install.exe from https://sourceforge.net/projects/mingw-w64/

2. Run the installer to install
	version: 6.2.0
	Architecture: i686
	Threads: posix
	Exception: dwarf
	Build revision: 0

3. Run the installer again to install
	version: 6.2.0
	Architecture: x86_64
	Threads: posix
	Exception: seh
	Build revision: 0
4. Setup Environmental variables
	Add following dirs to System variables: Path	
		C:\Program Files\mingw-w64\x86_64-6.2.0-posix-seh-rt_v5-rev0\mingw64\bin
		C:\Program Files (x86)\mingw-w64\i686-6.2.0-posix-dwarf-rt_v5-rev0\mingw32\bin

Setup IDE
1. Download eclipse-inst-win64.exe (neon version) from https://eclipse.org/downloads/

2. Run the installer to install
	Choose: Eclipse IDE for C/C++ Developers

3. Run Eclipse and locate workspace to BaseLine/c folder
	Import project from VideoCodec

4. Change configuration
	Project > Properties > C/C++ Build > Environment
