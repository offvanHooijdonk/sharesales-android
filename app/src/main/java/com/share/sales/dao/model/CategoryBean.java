package com.share.sales.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryBean extends BaseBean implements Parcelable {
	@Expose
	@SerializedName("nameCode")
	private String nameCode;
	@Expose
	@SerializedName("nameLocal")
	private String nameLocal;
	@Expose
	@SerializedName("parent")
	private CategoryBean parent;
	
	public String getNameCode() {
		return nameCode;
	}
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	public String getNameLocal() {
		return nameLocal;
	}
	public void setNameLocal(String nameLocal) {
		this.nameLocal = nameLocal;
	}
	public CategoryBean getParent() {
		return parent;
	}
	public void setParent(CategoryBean parent) {
		this.parent = parent;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(nameCode);
		dest.writeString(nameLocal);
	}
	
	private CategoryBean(Parcel in) {
        setId(in.readLong());
        nameCode = in.readString();
        nameLocal = in.readString();
    }
	
	public static final Parcelable.Creator<CategoryBean> CREATOR = new Parcelable.Creator<CategoryBean>() {
		public CategoryBean createFromParcel(Parcel in) {
		    return new CategoryBean(in);
		}
		
		public CategoryBean[] newArray(int size) {
		    return new CategoryBean[size];
		}
	};

}
