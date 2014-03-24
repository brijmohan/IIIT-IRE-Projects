import java.io.IOException;
import java.io.RandomAccessFile;

public class Search {
	/**
	 * Find the position of the start of the first line in the file that is
	 * greater than or equal to the target line, using a binary search.
	 * 
	 * @param file
	 *            the file to search.
	 * @param target
	 *            the target to find.
	 * @return the position of the first line that is greater than or equal to
	 *         the target line.
	 * @throws IOException
	 */
	public long findNearestOffset(RandomAccessFile file, String target) {
		/*
		 * because we read the second line after each seek there is no way the
		 * binary search will find the first line, so check it first.
		 */
		long filePointer = 0L;
		try {
			file.seek(0);

			String line = file.readLine();
			if (line == null || line.compareTo(target) >= 0) {
				/*
				 * the start is greater than or equal to the target, so it is
				 * what we are looking for.
				 */
				return 0;
			}

			/*
			 * set up the binary search.
			 */
			long beg = 0;
			long end = file.length();
			while (beg <= end) {
				/*
				 * find the mid point.
				 */
				long mid = beg + (end - beg) / 2;
				file.seek(mid);
				file.readLine();
				line = file.readLine();

				if (line == null || line.compareTo(target) >= 0) {
					/*
					 * what we found is greater than or equal to the target, so
					 * look before it.
					 */
					end = mid - 1;
				} else {
					/*
					 * otherwise, look after it.
					 */
					beg = mid + 1;
				}
			}

			/*
			 * The search falls through when the range is narrowed to nothing.
			 */
			file.seek(beg);
			file.readLine();
			filePointer = file.getFilePointer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filePointer;
	}

	/**
	 * For numbers
	 */
	public long findNearestOffset(RandomAccessFile file, long target)
			throws IOException {
		/*
		 * because we read the second line after each seek there is no way the
		 * binary search will find the first line, so check it first.
		 */
		long filePointer = 0L;
		try {
			file.seek(0);
			String line = file.readLine();
			long docId = Long.parseLong(line.substring(0, line.indexOf(" ")));
			if (line == null || docId >= target) {
				/*
				 * the start is greater than or equal to the target, so it is
				 * what we are looking for.
				 */
				return 0;
			}

			/*
			 * set up the binary search.
			 */
			long beg = 0;
			long end = file.length();
			while (beg <= end) {
				/*
				 * find the mid point.
				 */
				long mid = beg + (end - beg) / 2;
				file.seek(mid);
				file.readLine();
				line = file.readLine();
				docId = Long.parseLong(line.substring(0, line.indexOf(" ")));

				if (line == null || docId >= target) {
					/*
					 * what we found is greater than or equal to the target, so
					 * look before it.
					 */
					end = mid - 1;
				} else {
					/*
					 * otherwise, look after it.
					 */
					beg = mid + 1;
				}
			}

			/*
			 * The search falls through when the range is narrowed to nothing.
			 */
			file.seek(beg);
			file.readLine();
			filePointer = file.getFilePointer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePointer;
	}

	public long getPreviousLineStart(long lineStart, RandomAccessFile raf) {
		// Goto the end of previous line
		lineStart -= 2;
		try {
			raf.seek(lineStart);

			byte b = raf.readByte();

			while (b != 10) {
				lineStart--;
				if (lineStart < 0) {
					return 0;
				}
				raf.seek(lineStart);
				b = raf.readByte();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lineStart + 1;
	}

	public long getNextLineStart(long currentPointer, RandomAccessFile raf) {
		long filePointer = 0L;
		try {
			raf.seek(currentPointer);
			while (raf.readByte() != 10) {
			}
			filePointer = raf.getFilePointer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filePointer;
	}
}
