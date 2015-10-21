package com.share.sales.ui.sale.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apmem.tools.layouts.FlowLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.example.android.swipedismiss.SwipeDismissTouchListener;
import com.google.android.gms.maps.model.LatLng;
import com.share.sales.Constants;
import com.share.sales.R;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.dao.model.CurrencyBean;
import com.share.sales.dao.model.LocationBean;
import com.share.sales.dao.model.SalesBean;
import com.share.sales.dao.model.TagBean;
import com.share.sales.dao.service.CategoryDAO;
import com.share.sales.dao.service.CurrencyDAO;
import com.share.sales.dao.service.LocationDAO;
import com.share.sales.dao.service.SaleDAO;
import com.share.sales.dao.service.TagDAO;
import com.share.sales.exceptions.DuplicatedTagException;
import com.share.sales.helper.ColorsHelper;
import com.share.sales.helper.DiscountHelper;
import com.share.sales.helper.GsonHelper;
import com.share.sales.helper.ImagesHelper;
import com.share.sales.helper.TagsHelper;
import com.share.sales.ui.sale.category.CategoryPickerActivity;
import com.share.sales.ui.utils.ProgressBarUtil;
import com.share.sales.utils.FSUtil;
import com.share.sales.utils.LocationUtil;
import com.share.sales.web.client.ResponseBean;
import com.share.sales.web.client.ServiceCallListener;
import com.share.sales.web.client.impl.LocationRestClient;
import com.share.sales.web.client.impl.SaleRestClient;

public class SaleFormActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener, ServiceCallListener {
	private static final int REQUEST_PICK_IMAGE_FROM_GALERY = 1;
	private static final int REQUEST_TAKE_A_PICTURE = 2;
	private static final int REQUEST_LOCATION_PICK = 11;
	private static final int REQUEST_CATEGORY_PICK = 21;
	
	public static final String EXTRA_SALE_ID = "extra_sale_id";
	
	private static final String FRAG_TAG_DATE_START = "fragment_date_picker_start";
	private static final String FRAG_TAG_DATE_END = "fragment_date_picker_end";
	
	private static final String REST_TAG_SAVE = "request_tag_sale";
	private static final String REST_TAG_LOCATION = "request_tag_location";
	
	private LayoutInflater layoutInflater;
	private SaleFormActivity that;
	
	private ViewGroup blockPicsList;
	
	private ImageButton btnPickLocation;
	private TextView textStartDate;
	private TextView textEndDate;
	private TextView textCategory;
	
	private EditText inputName;
	private TextView textCity;
	private EditText inputAddress;
	private EditText inputStoreName;
	
	private EditText inputPerCent;
	private EditText inputOldPrice;
	private EditText inputCurrentPrice;
	private AutoCompleteTextView inputTagName;
	private FlowLayout blockTagsFlow;
	private Spinner ddCurrency;

	private LatLng locationPicked = null;
	private Date startDate = null;
	private Date endDate = null;
	private boolean wasDiscountCalculated = false;
	
	ProgressBarUtil progressUtil;
	private SalesBean saleBean;
	
	private LocationBean locationCountry;
	private LocationBean locationAdminArea;
	private LocationBean locationCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		that = this;
		progressUtil = new ProgressBarUtil(that, true);
		setContentView(R.layout.layout_sale_form);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		blockPicsList = (LinearLayout) findViewById(R.id.blockPicsList);
		textCategory = (TextView) findViewById(R.id.textCategory);
		inputName = (EditText) findViewById(R.id.inputName);
		textCity = (TextView) findViewById(R.id.inputCity);
		inputAddress = (EditText) findViewById(R.id.inputAdress);
		inputStoreName = (EditText) findViewById(R.id.inputStorePlace);
		
		textStartDate = (TextView) findViewById(R.id.textStartDate);
		textEndDate = (TextView) findViewById(R.id.textEndDate);
		
		inputPerCent = (EditText) findViewById(R.id.inputPerCent);
		inputOldPrice = (EditText) findViewById(R.id.inputOldPrice);
		inputCurrentPrice = (EditText) findViewById(R.id.inputCurrentPrice);
		blockTagsFlow = (FlowLayout) findViewById(R.id.blockTagsFlow);
		
		ddCurrency = (Spinner) findViewById(R.id.ddCurrency);
		
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Init bean object
		initBeanObject();
		
		// Colorize the discount amount
		initPriceInputsListeners();
		// Currency
		CurrencyDAO currencyDAO = new CurrencyDAO(that);
		List<CurrencyBean> currencies = currencyDAO.getAll();
		ddCurrency.setAdapter(new ArrayAdapter<CurrencyBean>(that, android.R.layout.simple_list_item_1, currencies));
		
		// Picking image
		initPicturePickers();
		
		// Picking location
		btnPickLocation = (ImageButton) findViewById(R.id.btnPickLocation);
		btnPickLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPickLocationDialog();
			}
		});
		
		// Picking dates
		textStartDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDatePicker(FRAG_TAG_DATE_START, startDate);
			}
		});
		
		textEndDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDatePicker(FRAG_TAG_DATE_END, endDate);
			}
		});
		
		// Category pick
		LinearLayout blockCatLayout = (LinearLayout) findViewById(R.id.blockCategory);
		blockCatLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(that, CategoryPickerActivity.class);
				that.startActivityForResult(intent, REQUEST_CATEGORY_PICK);
			}
		});
		
		//  Tags form
		initTagsForm();
		
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_SALE_ID)) {
			Long saleId = getIntent().getExtras().getLong(EXTRA_SALE_ID);
			if (saleId != SalesBean.VOID_ID) {
				SaleDAO dao = new SaleDAO(that);
				saleBean = dao.getById(saleId);
				if (saleBean != null) {
					displayBeanOnForm();
				} else {
					Toast.makeText(that, "Sale not found!", Toast.LENGTH_LONG).show();
					that.finish();
				}
			} else {
				Toast.makeText(that, "Incorrect data!", Toast.LENGTH_LONG).show();
				that.finish();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case REQUEST_PICK_IMAGE_FROM_GALERY : { // We picked smth from Gallery
				if (resultCode == RESULT_OK && data != null) {
					Uri selectedImage = data.getData();
			        String[] filePathColumn = { MediaStore.Images.Media.DATA };
			        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			        cursor.moveToFirst();
			        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			        String picturePath = cursor.getString(columnIndex);
			        cursor.close();
			        
			        File imgFile = new File(picturePath);
					
					if (imgFile.exists()) {
						try {
							addImageToBlock(picturePath, false);
						} catch(Exception e) {
							Toast.makeText(this, "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(this, "File not found: " + picturePath, Toast.LENGTH_LONG).show();
					}
				}
			} break;
			case REQUEST_TAKE_A_PICTURE : { // We took a picture with camera
				if (resultCode == RESULT_OK && data != null) {
					Bitmap photo = (Bitmap) data.getExtras().get("data");
					if (photo != null) {
						try {
							String path = FSUtil.saveCameraShot(photo);
							Toast.makeText(this, "Saved at: " + path, Toast.LENGTH_LONG).show();
							addImageToBlock(path, true);
						} catch(Exception e) {
							Toast.makeText(this, "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
						}
					}
				}
			} break;
			case REQUEST_LOCATION_PICK : { // We picked location with custom dialog fragment
				if (resultCode == RESULT_OK && data != null) {
					LatLng latLng = data.getExtras().getParcelable(MapPickerActivity.EXTRA_MAP_POSITION);
					if (latLng != null) {
						locationPicked = latLng;
						extractGeoData(latLng);
					} else {
						locationPicked = null;
						Toast.makeText(this, "Location mark removed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(this, "No data returned!", Toast.LENGTH_LONG).show();
				}
			} break;
			case REQUEST_CATEGORY_PICK : { //  We picked a category with a custom activity 
				if (resultCode == RESULT_OK && data != null) {
					List<CategoryBean> categoryHierarchy = data.getExtras().getParcelableArrayList(CategoryPickerActivity.EXTRA_CATEGORY_BEAN);
					if (categoryHierarchy != null && categoryHierarchy.size() > 0) {
						displayCategory(categoryHierarchy);
					}
				}
			} break;
			default:
				break;
		}
	}
	
	@Override
	public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		
		displayDate(calendar, dialog.getTag());
		 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_sale_form, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save) {
			startSaveProcess();
		} else if (item.getItemId() == android.R.id.home) {
			if (saleBean != null) {
				finish();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startSaveProcess() {
		boolean isValid = validateForm();
		if (isValid) {
			if (wasDiscountCalculated) {
				showDiscountCalculatedDialog();
			} else {
				try {
					progressUtil.startLoad();
					populateSaleBean();
					
				} catch (Exception e) {
					progressUtil.cancelProgress();
					Toast.makeText(that, e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		} else {
			progressUtil.cancelProgress();
		}
	}
	
	private boolean validateForm() {
		progressUtil.startShowProgress();
		boolean isValid = true;
		wasDiscountCalculated = false;
		
		// CATEGORY
		if (textCategory.getTag(R.string.tag_category_picked) == null) {
			isValid = false;
			textCategory.setError("Category is not picked");
		}
		
		// PRICES
		String currentPriceStr = inputCurrentPrice.getText().toString().trim();
		if ("".equals(currentPriceStr)) {
			isValid = false;
			inputCurrentPrice.setError("Please enter current price.");
			
		}
		String perCentStr = inputPerCent.getText().toString().trim();
		if ("".equals(perCentStr)) {
			// check if we can calculate discount
			String oldPriceStr = inputOldPrice.getText().toString().trim();
			if (!"".equals(oldPriceStr) && !"".equals(currentPriceStr)) {
				float currPrice = Float.parseFloat(currentPriceStr);
				float oldPrice = Float.parseFloat(oldPriceStr);
				int discount = DiscountHelper.calculateDiscount(currPrice, oldPrice);
				inputPerCent.setText(String.valueOf(discount));
				wasDiscountCalculated = true;
			} else {
				isValid = false;
				inputPerCent.setError("Please enter discount or both price values.");
			}
		}
		if (!"".equals(perCentStr)) {
			int discount = Integer.parseInt(perCentStr);
			if (discount > Constants.MAX_DISCOUNT) {
				isValid = false;
				inputPerCent.setError(String.format("The discount amount cannot be more than %s", Constants.MAX_DISCOUNT));
			}
			// TODO check that discount corresponds the price values allowing some inaccuracy
		}
		progressUtil.progressValidation(0.2f);
		// PICTURES
		HorizontalScrollView blockPicsScroll = (HorizontalScrollView) findViewById(R.id.blockPicsScroll);
		int picsCount = blockPicsList.getChildCount();
		File picFile;
		for (int i = 0; i < picsCount; i++) {
			ImageView iv = (ImageView) blockPicsList.getChildAt(i);
			String picFilePath = (String) iv.getTag(R.string.tag_file_path);
			picFile = new File(picFilePath);
			if (!picFile.exists()) {
				isValid = false;
				int width = iv.getWidth();
				int height = iv.getHeight();
				iv.setImageResource(R.drawable.ic_action_picture);
				iv.setBackgroundResource(R.drawable.bckgr_tag_default);
				iv.getLayoutParams().height = height;
				iv.getLayoutParams().width = width;
				iv.setScaleType(ScaleType.CENTER);
				iv.requestLayout();
				blockPicsScroll.requestChildFocus(iv, iv);
			}
		}
		progressUtil.progressValidation(0.5f);
		// Edit Views
		isValid = isValid && checkNotEmptyText(inputName, "Please enter name");
		// FIXME return city and what the hell with maps !!!!!!!
		//isValid = isValid && checkNotEmptyText(textCity, "Please enter city");
		isValid = isValid && checkNotEmptyText(inputAddress, "Please enter address");
		isValid = isValid && checkNotEmptyText(inputStoreName, "Please enter store name");
		progressUtil.progressValidation(0.7f);
		// DATES
		if (endDate != null) {
			Calendar calendarNow = Calendar.getInstance();
			Calendar calendarEnd = Calendar.getInstance();
			calendarEnd.setTime(endDate);
			if (calendarNow.after(calendarEnd)) {
				isValid = false;
				textEndDate.setError("Your end date is before today date.");
			}
			
			if (startDate != null) {
				Calendar calendarStart = Calendar.getInstance();
				calendarStart.setTime(startDate);
				if (calendarStart.after(calendarEnd)) {
					isValid = false;
					textStartDate.setError("Your start date is after the end date.");
				}
			}
		}
		progressUtil.progressValidation(1.0f);
		return isValid;
	}
	
	private void showDiscountCalculatedDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(that);
		alert.setTitle("Discount");
		alert.setMessage(String.format("We calaculated discount as %s%%. Do you agree?", inputPerCent.getText().toString()));
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startSaveProcess();
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressUtil.cancelProgress();
				dialog.dismiss();
				inputPerCent.requestFocus();
			}
		});
		alert.show();
	}
	
	private void populateSaleBean() {
		saleBean.setName(inputName.getText().toString().trim());
		saleBean.setAddress(inputAddress.getText().toString().trim());
		if (startDate != null) {
			saleBean.setStartDate(startDate.getTime());
		}
		if (endDate != null) {
			saleBean.setEndDate(endDate.getTime());
		}
		
		progressUtil.progressLoad(0.1f);
		// Category
		CategoryBean category = (CategoryBean) textCategory.getTag(R.string.tag_category_picked);
		saleBean.setCategory(category);
		// Prices & currency
		CurrencyBean currency = (CurrencyBean) ddCurrency.getSelectedItem();
		saleBean.setCurrency(currency);
		saleBean.setPriceCurrent(Float.parseFloat(inputCurrentPrice.getText().toString()));
		saleBean.setPriceOrigin(inputOldPrice.getText().toString().equals("") ? 0 : Float.parseFloat(inputOldPrice.getText().toString()));
		saleBean.setPerCent(Integer.parseInt(inputPerCent.getText().toString()));
		
		progressUtil.progressLoad(0.2f);
		
		// TAGS
		int tagsCount = blockTagsFlow.getChildCount() - 1; // last element is the AutoCompleteTextView
		List<TagBean> tags = new ArrayList<TagBean>();
		for (int i = 0; i < tagsCount; i++) {
			tags.add((TagBean) blockTagsFlow.getChildAt(i).getTag(R.string.tag_tag_bean));
		}
		if (!tags.isEmpty()) {
			saleBean.setTagsString(TagsHelper.createTagsString(tags));
		} else {
			saleBean.setTagsString(null);
		}
		progressUtil.progressLoad(0.4f);
		
		// IMAGES
		saleBean.setPictures(new ArrayList<String>());
		int picsCount = blockPicsList.getChildCount();
		for (int i = 0; i < picsCount; i++) {
			ImageView iv = (ImageView) blockPicsList.getChildAt(i);
			String picFilePath = (String) iv.getTag(R.string.tag_file_path);
			Boolean isTmp = (Boolean) iv.getTag(R.string.tag_is_tmp);
			
			String uploadFilePath;
			try {
				uploadFilePath = FSUtil.storeFileToUploadFolder(picFilePath, isTmp);
			} catch (FileNotFoundException e) {
				// TODO make solid user-aware handling
				Toast.makeText(that, e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			} catch (IOException e) {
				// TODO make solid user-aware handling
				Toast.makeText(that, e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
			saleBean.getPictures().add(uploadFilePath);
		}
		progressUtil.progressLoad(0.6f);
		
		// CITY and Store - this goes the last as performs service async call
		// TODO need to make this better due to we can have to make multiple async calls 
		/*locationPicked = new LatLng(52.4065478, 30.924303);
		extractGeoData(locationPicked);*/
		// ----
		if (locationPicked != null) {
			/*LocationDAO dao = new LocationDAO(that);
			LocationBean city = dao.getOrSaveCity(locationCountry, locationAdminArea, locationCity);
			saleBean.setCity(city);*/
			
			saleBean.setLatitude(locationPicked.latitude);
			saleBean.setLongitude(locationPicked.longitude);
			
			locationAdminArea.setParent(locationCountry);
			locationCity.setParent(locationAdminArea);
			
			new LocationRestClient(that, that).getOrSaveLocation(locationCity, REST_TAG_LOCATION);
		}
		/*StoreDAO storeDAO = new StoreDAO(that);
		String storeName = inputStoreName.getText().toString().trim();
		if (!"".equals(storeName)) {
			StoreBean storeBean = new StoreBean();
			storeBean.setNameLocal(storeName);
			storeBean = storeDAO.save(storeBean);
			saleBean.setStore(storeBean);
		}*/
	}
	
	private void displayBeanOnForm() {
		inputName.setText(saleBean.getName());
		// Category
		CategoryBean category = saleBean.getCategory();
		CategoryDAO categoryDAO = new CategoryDAO(that);
		List<CategoryBean> catHierarchy = categoryDAO.getParentsHierarchy(category);
		category = categoryDAO.getById(category.getId()); //  currently category might be saved with ID only
		catHierarchy.add(category);
		displayCategory(catHierarchy);
		// Prices
		inputCurrentPrice.setText(String.valueOf(saleBean.getPriceCurrent()));
		if (saleBean.getPriceOrigin() != 0.0f) {
			inputOldPrice.setText(String.valueOf(saleBean.getPriceOrigin()));
		}
		inputPerCent.setText(String.valueOf(saleBean.getPerCent()));
		colorizePerCent();
		// currency
		int currCount = ddCurrency.getCount();
		for (int i = 0; i < currCount; i++) {
			CurrencyBean currencyBean = (CurrencyBean) ddCurrency.getItemAtPosition(i);
			if (currencyBean.getId() == saleBean.getCurrency().getId()) {
				ddCurrency.setSelection(i);
				break;
			}
		}
		// location & store
		if (saleBean.getLatitude() != null && saleBean.getLongitude() != null) {
			locationPicked = new LatLng(saleBean.getLatitude(), saleBean.getLongitude());
			extractGeoData(locationPicked);
		}
		if (saleBean.getStore() != null) {
			inputStoreName.setText(saleBean.getStore().getNameLocal());
		} else {
			// FIXME temp
			inputStoreName.setText("text for validation");
			inputAddress.setText(saleBean.getAddress());
		}
		
		// dates
		if (saleBean.getStartDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(saleBean.getStartDate());
			displayDate(calendar, FRAG_TAG_DATE_START);
		}
		if (saleBean.getEndDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(saleBean.getEndDate());
			displayDate(calendar, FRAG_TAG_DATE_END);
		}
		// images
		if (saleBean.getPictures() != null) {
			for (String filePath : saleBean.getPictures()) {
				try {
					addImageToBlock(filePath, true);
				} catch (FileNotFoundException e) {
					Toast.makeText(that, "File not found: " + filePath, Toast.LENGTH_LONG).show();
				}
			}
		}
		// TODO Tags
		List<TagBean> tags = TagsHelper.createTagsList(saleBean.getTagsString(), that);
		for (TagBean tagBean : tags) {
			try {
				addTagView(tagBean, false);
			} catch (DuplicatedTagException e) {
				// TODO log
			}
		}
	}
	
	private boolean checkNotEmptyText(TextView editText, String errorMsg) {
		boolean valid = true;
		
		String text = editText.getText().toString().trim();
		
		if ("".equals(text)) {
			valid = false;
			editText.setError(errorMsg);
		}
		
		return valid;
	}
	
	private void initBeanObject() {
		saleBean = new SalesBean();
		saleBean.setTags(new ArrayList<TagBean>());
		/*saleBean.setPictures(new ArrayList<String>());*/
	}
	
	/**
	 * Set listeners to handle focus loss on price inputs
	 */
	private void initPriceInputsListeners() {
		inputOldPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				onPriceInputFocusChanged();
			}
		});
		inputCurrentPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				onPriceInputFocusChanged();
			}
		});
		inputPerCent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				colorizePerCent();
			}
		});
	}
	
	/**
	 * Init picture picker menu and handlers
	 */
	private void initPicturePickers() {
		ImageButton btnAddImage = (ImageButton) findViewById(R.id.btnAddImage);
		final PopupMenu popup = new PopupMenu(this, btnAddImage);
        popup.getMenuInflater().inflate(R.menu.photo_button_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	switch (item.getItemId()) {
					case R.id.action_shoot : {
						takeAPicture();
					} break;
					case R.id.action_upload : {
						addImageFromGallery();
					} break;
	
					default:
						break;
				}
                return true;
            }
        });
		btnAddImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.show();
			}
		});
	}
	
	/**
	 * Init tags input and tag swipe listeners
	 */
	private void initTagsForm() {
		inputTagName = (AutoCompleteTextView) findViewById(R.id.inputTagName);
		
		List<TagBean> tags = (new TagDAO(this)).getAll();
		TagsAutoCompleteAdapter adapter = new TagsAutoCompleteAdapter(tags);
		inputTagName.setAdapter(adapter);
		inputTagName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TagBean tag = ((TagsAutoCompleteAdapter) parent.getAdapter()).getFilteredList().get(position);
				
				try {
					addTagView(tag, true);
				} catch (DuplicatedTagException e) {
					Toast.makeText(that, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		inputTagName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String tagText = inputTagName.getText().toString().trim();
					if (!"".equals(tagText)) {
						TagBean tagBean = new TagBean();
						tagBean.setTitle(tagText);
						
						try {
							addTagView(tagBean, true);
						} catch (DuplicatedTagException e) {
							Toast.makeText(that, e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
					handled = true;
		        }
				return handled;
			}
		});
		blockTagsFlow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					inputTagName.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
				}
			}
		});
	}
	
	/**
	 * Tunes TextView with a tag and adds it to the tags block
	 * @param tv
	 * @param bean
	 * @throws DuplicatedTagException 
	 */
	private void addTagView(TagBean bean, boolean checkUnique) throws DuplicatedTagException {
		TextView tagView = (TextView) layoutInflater.inflate(R.layout.li_tag, blockTagsFlow, false);
		if (checkUnique && saleBean.getTags().contains(bean)) {
			throw new DuplicatedTagException(String.format("Tag %s already added.", bean.getTitle()));
		}
		
		tagView = TagsHelper.settleTagTextView(bean, tagView);
		tagView.setTag(R.string.tag_tag_bean, bean);
		attachDismissActionToTag(tagView);
		saleBean.getTags().add(bean);
		
		blockTagsFlow.addView(tagView, blockTagsFlow.getChildCount() - 1);
		inputTagName.getText().clear();
	}
	
	/**
	 * Adds listener to a text view which enables Swipe To Dismiss (horizontal)
	 * @param view
	 */
	private void attachDismissActionToTag(View view) {
		view.setOnTouchListener(new SwipeDismissTouchListener(view, null, new SwipeDismissTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(View view, Object token) {
				saleBean.getTags().remove(token);
				
				blockTagsFlow.removeView(view);
			}
			
			@Override
			public boolean canDismiss(Object token) {
				return true;
			}
		}));
	}
	
	/**
	 * Attaches listener to an ImageView which enables SwipeToDismiss (vertical)
	 * @param view
	 */
	private void attachDismissActionToImage(ImageView view) {
		view.setOnTouchListener(new SwipeDismissTouchListener(view, null, SwipeDismissTouchListener.SWIPE_DIRECTION_VERTICAL, 
				new SwipeDismissTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(View view, Object token) {
				String filePath = view.getTag(R.string.tag_file_path).toString();
				boolean isTmpFile = (Boolean) view.getTag(R.string.tag_is_tmp);
				if (filePath != null && isTmpFile) {
					File file = new File(filePath);
					if (file.exists()) {
						file.delete();
					}
				}
				//saleBean.getPictures().remove(filePath);
				
				blockPicsList.removeView(view);
			}
			
			@Override
			public boolean canDismiss(Object token) {
				return true;
			}
		}));
	}
	
	/**
	 * If both prices are put in - calculate sale per cent and change Per Cent input text and color 
	 */
	private void onPriceInputFocusChanged() {
		String oldPriceStr = inputOldPrice.getText().toString();
		String currentPriceStr = inputCurrentPrice.getText().toString();
		
		if (!"".equals(oldPriceStr) && !"".equals(currentPriceStr)) {
			Float oldPrice = Float.parseFloat(oldPriceStr);
			Float currentPrice = Float.parseFloat(currentPriceStr);
			if(checkPricesValid(oldPrice, currentPrice)) {
				int perCent = DiscountHelper.calculateDiscount(currentPrice, oldPrice);
				inputPerCent.setText(String.valueOf(perCent));
				colorizePerCent();
			} else {
				inputPerCent.setText("");
				colorizePerCent();
			}
		}
	}
	/**
	 * Check if values of prices correspond their meaning (not 0 and new price is less)
	 * @param oldPrice
	 * @param newPrice
	 * @return
	 */
	
	private boolean checkPricesValid(float oldPrice, float newPrice) {
		return !(oldPrice == 0 || newPrice == 0 || newPrice >= oldPrice);
	}
	
	/**
	 * if there is text in Per Cent input - change its color depending on value
	 */
	private void colorizePerCent() {
		String perCentStr = inputPerCent.getText().toString();
		if (!"".equals(perCentStr)) {
			int perCent = Integer.parseInt(perCentStr);
			int color = ColorsHelper.getPerCentColor(that, perCent);
			inputPerCent.setTextColor(color);
		} else {
			inputPerCent.setTextColor(Color.BLACK);
		}
	}
	
	/**
	 * Start activity with a map and picking tools
	 */
	private void startPickLocationDialog() {
		Intent intent = new Intent(this, MapPickerActivity.class);
		intent.putExtra(MapPickerActivity.EXTRA_INPUT_MAP_POSITION, locationPicked);
		startActivityForResult(intent, REQUEST_LOCATION_PICK);
	}
	
	/**
	 * Start intent to take a photo with a camera
	 */
	private void takeAPicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_TAKE_A_PICTURE);
	}
	
	/**
	 * Start intent to pick an image from Gallery
	 */
	private void addImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_PICK_IMAGE_FROM_GALERY);
	}
	
	/**
	 * Process location to get location units and store/display them
	 * @param latLng
	 */
	private void extractGeoData(LatLng latLng) {
		Geocoder geocoder = new Geocoder(that);
		Geocoder geocoderUS = new Geocoder(that, Locale.US);
		try {
			List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 3); // TODO hardcoded!
			List<Address> addressesUS = geocoderUS.getFromLocation(latLng.latitude, latLng.longitude, 3); // TODO hardcoded!
			if (addresses != null && addresses.size() > 0) {
				// displayTmpGeoData();
				Address address = addresses.get(0);
				Address addressUS = addressesUS.get(0);
				
				String street = address.getThoroughfare();
				street = street == null ? "" : street;

				String placeNum = address.getSubThoroughfare();
				placeNum = placeNum == null ? "" : placeNum;
				
				
				textCity.setText(address.getCountryCode() + ", " + address.getLocality() + " (" + addressUS.getLocality() + ")");
				inputAddress.setText(street + " " + placeNum/* + "|" + String.format("%s,%s", latLng.latitude, latLng.longitude)*/);
				
				if (locationCountry == null) {
					locationCountry = new LocationBean();
				}
				if (locationAdminArea == null) {
					locationAdminArea = new LocationBean();
				}
				if (locationCity == null) {
					locationCity = new LocationBean();
				}
				
				locationCountry.setNameCode(address.getCountryCode());
				String countryNameLocal = LocationUtil.getLocalCountryName(that, address.getCountryCode(), latLng);
				if (countryNameLocal != null) {
					locationCountry.setNameLocal(countryNameLocal);
				} else {
					locationCountry.setNameLocal(address.getCountryName());
				}
				
				locationAdminArea.setNameCode(addressUS.getAdminArea());
				String adminAreaLocal = LocationUtil.getLocalAdminArea(that, address.getCountryCode(), latLng);
				if (adminAreaLocal != null) {
					locationAdminArea.setNameLocal(adminAreaLocal);
				} else {
					locationAdminArea.setNameLocal(address.getAdminArea());
				}
				
				locationCity.setNameCode(addressUS.getLocality());
				String cityNameLocal = LocationUtil.getLocalCityName(that, address.getCountryCode(), latLng);
				if (cityNameLocal != null) {
					locationCity.setNameLocal(cityNameLocal);
				} else {
					locationCity.setNameLocal(address.getLocality());
				}
				
			} else {
				Toast.makeText(this, "No address found at the location.", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			Toast.makeText(this, "Error getting Addresses: " + e.toString(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	/**
	 * Display picture in a pictures block
	 * @param bitmap
	 * @param filePath path to a temp file if the image is associated with a temp file
	 * @throws FileNotFoundException 
	 */
	private void addImageToBlock(String filePath, boolean isTmpFile) throws FileNotFoundException {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) layoutInflater.inflate(R.layout.li_image_on_form, blockPicsList, false);
		
		int height = that.getResources().getDimensionPixelSize(R.dimen.form_images_block_height);//blockPicsList.getHeight();
		Bitmap bitmap = ImagesHelper.createScaledBitmapByHight(filePath, height);
		iv.setImageBitmap(bitmap);
	    
    	iv.setTag(R.string.tag_file_path, filePath);
    	iv.setTag(R.string.tag_is_tmp, Boolean.valueOf(isTmpFile));
    	
	    attachDismissActionToImage(iv);
	    
	    blockPicsList.addView(iv);
	}
	
	/**
	 * Starts date picker dialog
	 * @param tag
	 */
	private void showDatePicker(String tag, Date date) { // TODO : hide keyboard
		FragmentManager fm = that.getSupportFragmentManager();
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}
		CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(that, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                		calendar.get(Calendar.DAY_OF_MONTH));
        calendarDatePickerDialog.show(fm, tag);
	}
	
	private void displayCategory(List<CategoryBean> categoryHierarchy) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < categoryHierarchy.size(); i++) {
			str.append(categoryHierarchy.get(i).getNameLocal());
			if (i != categoryHierarchy.size() - 1) {
				str.append(" > ");
			}
		}
		textCategory.setTag(R.string.tag_category_picked, categoryHierarchy.get(categoryHierarchy.size() - 1));
		textCategory.setText(str.toString());
	}
	
	private void displayDate(Calendar calendar, String tag) {
		String text = DateUtils.formatDateTime(this, calendar.getTimeInMillis(), Constants.DATE_FLAGS_DATE_DIGIT_YEAR);
		
		if (FRAG_TAG_DATE_START.equals(tag)) {
			textStartDate.setText(text);
			startDate = calendar.getTime();
		} else if (FRAG_TAG_DATE_END.equals(tag)) {
			textEndDate.setText(text);
			endDate = calendar.getTime();
		}
	}

	@Override
	public void handleResponse(ResponseBean responseBean) {
		if (REST_TAG_SAVE.equals(responseBean.getTag())) {
			onSaveResponse(responseBean);
		} else if (REST_TAG_LOCATION.equals(responseBean.getTag())) {
			onLocationResponse(responseBean);
		}
		
		
	}
	
	private void onSaveResponse(ResponseBean responseBean) {
		if (responseBean.isFailed()) {
			progressUtil.finishProgress();
			Toast.makeText(that, "Not saved! " + responseBean.getException().toString(), Toast.LENGTH_LONG).show();
		} else if (!responseBean.isValidResponseCode()) {
			progressUtil.finishProgress();
			Toast.makeText(that, "Error: invalid response code " + responseBean.getCode(), Toast.LENGTH_LONG).show();
		} else {
			if (REST_TAG_SAVE.equals(responseBean.getTag())) {
				SaleDAO saleDAO = new SaleDAO(that);
				SalesBean bean = GsonHelper.getGson().fromJson(responseBean.getBody(), SalesBean.class);
				saleBean = bean;
				saleDAO.update(saleBean);
				
				LocationBean city = bean.getCity();
				
				LocationDAO locationDAO = new LocationDAO(that);
				locationDAO.saveHierarchy(city.getParent().getParent(), city.getParent(), city);
				
				progressUtil.finishProgress();
				Toast.makeText(that, "Saved ) ", Toast.LENGTH_LONG).show();
				that.finish();
			}
		}
	}
	
	private void onLocationResponse(ResponseBean responseBean) {
		if (responseBean.isFailed()) {
			
		} else if (!responseBean.isValidResponseCode()) {
			
		} else {
			progressUtil.progressLoad(0.7f);
			LocationBean location = GsonHelper.getGson().fromJson(responseBean.getBody(), LocationBean.class);
			
			saleBean.setCity(location);
			
			new SaleRestClient(that, that).save(saleBean, REST_TAG_SAVE);
		}
	}

	
	/*private void displayTmpGeoData() {
		// debug see content
				try {
					EditText text = (EditText) findViewById(R.id.textLocTmp);
					
					text.getText().append("Total addresses : ").append(String.valueOf(addresses.size())).append("\n");
					
					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						text.getText().append(String.valueOf(i)).append(" : ").append(address.getAddressLine(i));
						text.getText().append("\n");
					}
					StringBuilder str = new StringBuilder();
					str.append("AdminArea : ").append(address.getAdminArea()).append("\n");
					str.append("CountryCode : ").append(address.getCountryCode()).append("\n");
					str.append("CountryName : ").append(address.getCountryName()).append("\n");
					str.append("FeatureName : ").append(address.getFeatureName()).append("\n");
					str.append("Locality : ").append(address.getLocality()).append("\n");
					str.append("Phone : ").append(address.getPhone()).append("\n");
					str.append("PostalCode : ").append(address.getPostalCode()).append("\n");
					str.append("Premises : ").append(address.getPremises()).append("\n");
					str.append("SubAdminArea : ").append(address.getSubAdminArea()).append("\n");
					str.append("SubLocality : ").append(address.getSubLocality()).append("\n");
					str.append("SubThoroughfare : ").append(address.getSubThoroughfare()).append("\n");
					str.append("Thoroughfare : ").append(address.getThoroughfare()).append("\n");
					str.append("Url : ").append(address.getUrl()).append("\n");
					text.getText().append(str.toString());
				} catch (Exception e) {
					Toast.makeText(this, "Exceprion: " + e.toString(), Toast.LENGTH_LONG).show();
				}
				// ---
	}*/
}
