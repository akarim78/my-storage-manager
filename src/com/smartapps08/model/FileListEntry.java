package com.smartapps08.model;

public class FileListEntry {

	private String path;
	private String parentPath;
	private String name;
	private String size;
	private String modified;
	private boolean isDir;

	public FileListEntry() {
	}

	public FileListEntry(String path, String parentPath, String name,
			String size, String modified, boolean isDir) {
		this.path = path;
		this.parentPath = parentPath;
		this.name = name;
		this.size = size;
		this.modified = modified;
		this.isDir = isDir;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	@Override
	public String toString() {
		return "FileListEntry [path=" + path + ", parentPath=" + parentPath
				+ ", name=" + name + ", size=" + size + ", modified="
				+ modified + ", isDir=" + isDir + "]";
	}

}
