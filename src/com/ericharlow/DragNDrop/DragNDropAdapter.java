/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericharlow.DragNDrop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;

public final class DragNDropAdapter extends SimpleAdapter implements RemoveListener, DropListener{
	private List<HashMap<String, Object>> data;
    
    public DragNDropAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
	}

//    public DragNDropAdapter(Context context, int[] itemLayouts, int[] itemIDs, ArrayList<String> content) {
//    	init(context,itemLayouts,itemIDs, content);
//    }


	public void onRemove(int which) {
		if (data==null) return;
		if  (which<0 || which>=data.size()) return;
		data.remove(which);
		
//		if (which < 0 || which > mContent.size()) return;		
//		mContent.remove(which);
		notifyDataSetChanged();
	}

	public void onDrop(int from, int to) {
		HashMap<String, Object> temp=data.get(from);
		data.remove(from);
		data.add(to, temp);
		notifyDataSetChanged();
//		String temp = mContent.get(from);
//		mContent.remove(from);
//		mContent.add(to,temp);
	}

	public void setData(List<HashMap<String, Object>> data) {
		this.data=data;
	}
}