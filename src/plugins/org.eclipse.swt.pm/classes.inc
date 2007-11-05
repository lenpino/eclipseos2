# The list of files to include in compilation.
# Comment lines show an SWT part name from which the following classes are.
# A sharp (#) sign means that classes are platform dependent and located in
# the repository rather that in the original Eclipse distribution.
# A plus (+) sign means that classes are common but need an adaptation to OS/2
# so also located in the repository.
# Other classes are from the common part and taken from the original distribution.

# SWT Base (#)

org\eclipse\swt\graphics\Device.java
org\eclipse\swt\graphics\DeviceData.java
org\eclipse\swt\graphics\GC.java
org\eclipse\swt\graphics\GCData.java
org\eclipse\swt\graphics\Color.java
org\eclipse\swt\graphics\Region.java
org\eclipse\swt\graphics\Image.java
org\eclipse\swt\graphics\Font.java
org\eclipse\swt\graphics\FontData.java
org\eclipse\swt\graphics\FontMetrics.java

org\eclipse\swt\widgets\Display.java
org\eclipse\swt\widgets\Widget.java
org\eclipse\swt\widgets\WidgetTable.java
org\eclipse\swt\widgets\Control.java
org\eclipse\swt\widgets\Scrollable.java
org\eclipse\swt\widgets\Composite.java
org\eclipse\swt\widgets\Canvas.java
org\eclipse\swt\widgets\Decorations.java
org\eclipse\swt\widgets\Shell.java
org\eclipse\swt\widgets\ClientArea.java
org\eclipse\swt\widgets\Button.java
org\eclipse\swt\widgets\MessageBox.java
org\eclipse\swt\widgets\FontDialog.java
org\eclipse\swt\widgets\FileDialog.java
org\eclipse\swt\widgets\ColorDialog.java
org\eclipse\swt\widgets\Label.java
org\eclipse\swt\widgets\ScrollBar.java
org\eclipse\swt\widgets\Text.java
org\eclipse\swt\widgets\List.java
org\eclipse\swt\widgets\Combo.java
org\eclipse\swt\widgets\Slider.java
org\eclipse\swt\widgets\TabFolder.java
org\eclipse\swt\widgets\TabItem.java
org\eclipse\swt\widgets\Group.java
# SWT Base

org\eclipse\swt\graphics\Drawable.java
org\eclipse\swt\graphics\Point.java
org\eclipse\swt\graphics\Rectangle.java
org\eclipse\swt\graphics\RGB.java
org\eclipse\swt\graphics\ImageData.java
org\eclipse\swt\graphics\ImageLoader.java
org\eclipse\swt\graphics\ImageLoaderEvent.java
org\eclipse\swt\graphics\ImageLoaderListener.java
org\eclipse\swt\graphics\PaletteData.java

org\eclipse\swt\SWT.java
org\eclipse\swt\SWTError.java
org\eclipse\swt\SWTException.java

org\eclipse\swt\internal\Callback.java

org\eclipse\swt\internal\image\FileFormat.java
org\eclipse\swt\internal\image\GIFFileFormat.java
org\eclipse\swt\internal\image\JPEGAppn.java
org\eclipse\swt\internal\image\JPEGArithmeticConditioningTable.java
org\eclipse\swt\internal\image\JPEGComment.java
org\eclipse\swt\internal\image\JPEGEndOfImage.java
org\eclipse\swt\internal\image\JPEGFileFormat.java
org\eclipse\swt\internal\image\JPEGFixedSizeSegment.java
org\eclipse\swt\internal\image\JPEGFrameHeader.java
org\eclipse\swt\internal\image\JPEGHuffmanTable.java
org\eclipse\swt\internal\image\JPEGQuantizationTable.java
org\eclipse\swt\internal\image\JPEGRestartInterval.java
org\eclipse\swt\internal\image\JPEGScanHeader.java
org\eclipse\swt\internal\image\JPEGSegment.java
org\eclipse\swt\internal\image\JPEGStartOfImage.java
org\eclipse\swt\internal\image\JPEGVariableSizeSegment.java
org\eclipse\swt\internal\image\LEDataInputStream.java
org\eclipse\swt\internal\image\LEDataOutputStream.java
org\eclipse\swt\internal\image\LZWCodec.java
org\eclipse\swt\internal\image\LZWNode.java
org\eclipse\swt\internal\image\PngChunk.java
org\eclipse\swt\internal\image\PngChunkReader.java
org\eclipse\swt\internal\image\PngDecodingDataStream.java
org\eclipse\swt\internal\image\PNGFileFormat.java
org\eclipse\swt\internal\image\PngFileReadState.java
org\eclipse\swt\internal\image\PngHuffmanTable.java
org\eclipse\swt\internal\image\PngHuffmanTables.java
org\eclipse\swt\internal\image\PngIdatChunk.java
org\eclipse\swt\internal\image\PngIendChunk.java
org\eclipse\swt\internal\image\PngIhdrChunk.java
org\eclipse\swt\internal\image\PngLzBlockReader.java
org\eclipse\swt\internal\image\PngPlteChunk.java
org\eclipse\swt\internal\image\PngTrnsChunk.java
org\eclipse\swt\internal\image\WinBMPFileFormat.java
org\eclipse\swt\internal\image\WinICOFileFormat.java

org\eclipse\swt\widgets\Event.java
org\eclipse\swt\widgets\EventTable.java
org\eclipse\swt\widgets\Listener.java
org\eclipse\swt\widgets\TypedListener.java
org\eclipse\swt\widgets\RunnableLock.java
org\eclipse\swt\widgets\Synchronizer.java
org\eclipse\swt\widgets\Layout.java
org\eclipse\swt\widgets\Dialog.java

org\eclipse\swt\layout\FillLayout.java
org\eclipse\swt\layout\FormAttachment.java
org\eclipse\swt\layout\FormData.java
org\eclipse\swt\layout\FormLayout.java
org\eclipse\swt\layout\GridData.java
org\eclipse\swt\layout\GridLayout.java
org\eclipse\swt\layout\RowData.java
org\eclipse\swt\layout\RowLayout.java

org\eclipse\swt\events\TypedEvent.java
org\eclipse\swt\events\PaintEvent.java
org\eclipse\swt\events\PaintListener.java
org\eclipse\swt\events\SelectionEvent.java
org\eclipse\swt\events\SelectionListener.java
org\eclipse\swt\events\SelectionAdapter.java
org\eclipse\swt\events\DisposeEvent.java
org\eclipse\swt\events\DisposeListener.java
org\eclipse\swt\events\FocusEvent.java
org\eclipse\swt\events\FocusListener.java
org\eclipse\swt\events\FocusAdapter.java
org\eclipse\swt\events\MenuEvent.java
org\eclipse\swt\events\MenuListener.java
org\eclipse\swt\events\MenuAdapter.java
org\eclipse\swt\events\KeyEvent.java
org\eclipse\swt\events\KeyListener.java
org\eclipse\swt\events\KeyAdapter.java
org\eclipse\swt\events\MouseEvent.java
org\eclipse\swt\events\MouseListener.java
org\eclipse\swt\events\MouseMoveListener.java
org\eclipse\swt\events\MouseAdapter.java
org\eclipse\swt\events\MouseTrackListener.java
org\eclipse\swt\events\MouseTrackAdapter.java
org\eclipse\swt\events\ControlEvent.java
org\eclipse\swt\events\ControlListener.java
org\eclipse\swt\events\ControlAdapter.java
org\eclipse\swt\events\ShellEvent.java
org\eclipse\swt\events\ShellListener.java
org\eclipse\swt\events\ShellAdapter.java
org\eclipse\swt\events\TreeEvent.java
org\eclipse\swt\events\TreeListener.java
org\eclipse\swt\events\TreeAdapter.java
org\eclipse\swt\events\ModifyEvent.java
org\eclipse\swt\events\ModifyListener.java
org\eclipse\swt\events\VerifyEvent.java
org\eclipse\swt\events\VerifyListener.java
org\eclipse\swt\events\HelpEvent.java
org\eclipse\swt\events\HelpListener.java
org\eclipse\swt\events\ArmEvent.java
org\eclipse\swt\events\ArmListener.java
org\eclipse\swt\events\TraverseEvent.java
org\eclipse\swt\events\TraverseListener.java

# SWT Base_se

org\eclipse\swt\internal\CloneableCompatibility.java
org\eclipse\swt\internal\Compatibility.java
org\eclipse\swt\internal\SerializableCompatibility.java
org\eclipse\swt\internal\SWTEventListener.java
org\eclipse\swt\internal\SWTEventObject.java

# SWT PI (#)

org\eclipse\swt\internal\pm\OS.java
org\eclipse\swt\internal\pm\PSZ.java
#org\eclipse\swt\internal\pm\MPARAM.java
org\eclipse\swt\internal\pm\QMSG.java
org\eclipse\swt\internal\pm\TIB.java
org\eclipse\swt\internal\pm\TIB2.java
org\eclipse\swt\internal\pm\PIB.java
org\eclipse\swt\internal\pm\CLASSINFO.java
org\eclipse\swt\internal\pm\RECTL.java
org\eclipse\swt\internal\pm\MRESULT.java
org\eclipse\swt\internal\pm\FRAMECDATA.java
org\eclipse\swt\internal\pm\RGNRECT.java
org\eclipse\swt\internal\pm\BITMAPINFOHEADER2.java
org\eclipse\swt\internal\pm\POINTERINFO.java
org\eclipse\swt\internal\pm\FATTRS.java
org\eclipse\swt\internal\pm\FONTMETRICS.java
org\eclipse\swt\internal\pm\FACENAMEDESC.java
org\eclipse\swt\internal\pm\FILEDLG.java
org\eclipse\swt\internal\pm\FONTDLG.java
org\eclipse\swt\internal\pm\SBCDATA.java
org\eclipse\swt\internal\pm\LBOXINFO.java
# SWT PI (+)

org\eclipse\swt\internal\Library.java

# SWT Accessibility

org\eclipse\swt\accessibility\ACC.java
org\eclipse\swt\accessibility\AccessibleAdapter.java
org\eclipse\swt\accessibility\AccessibleControlAdapter.java
org\eclipse\swt\accessibility\AccessibleControlEvent.java
org\eclipse\swt\accessibility\AccessibleControlListener.java
org\eclipse\swt\accessibility\AccessibleEvent.java
org\eclipse\swt\accessibility\AccessibleListener.java
# emulated
org\eclipse\swt\accessibility\Accessible.java


