package com.smartapps08.adapters;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartapps08.model.FileListEntry;
import com.smartapps08.storage.R;
import com.smartapps08.util.Util;

public class FileListAdapter extends BaseAdapter {

	public static class ViewHolder {
		public TextView resName;
		public ImageView resIcon;
		public TextView resMeta;
	}

	private static final String TAG = FileListAdapter.class.getName();

	private Activity mContext;
	private List<FileListEntry> files;
	private LayoutInflater mInflater;

	public FileListAdapter(Activity context,
			List<FileListEntry> files) {
		super();
		mContext = context;
		this.files = files;
		mInflater = mContext.getLayoutInflater();
	}

	@Override
	public int getCount() {
		if (files == null) {
			return 0;
		} else {
			return files.size();
		}
	}

	@Override
	public Object getItem(int arg0) {

		if (files == null)
			return null;
		else
			return files.get(arg0);
	}

	public List<FileListEntry> getItems() {
		return files;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.explorer_item, parent,
					false);
			holder = new ViewHolder();
			holder.resName = (TextView) convertView
					.findViewById(R.id.explorer_resName);
			holder.resMeta = (TextView) convertView
					.findViewById(R.id.explorer_resMeta);
			holder.resIcon = (ImageView) convertView
					.findViewById(R.id.explorer_resIcon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FileListEntry currentFile = files.get(position);
		Log.e(TAG, currentFile.toString());
		holder.resName.setText(currentFile.getName());
		holder.resIcon.setImageDrawable(Util.getIcon(mContext,
				currentFile));
		holder.resMeta.setText(currentFile.getSize());

		return convertView;
	}
}
