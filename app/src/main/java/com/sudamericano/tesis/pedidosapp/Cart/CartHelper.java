package com.sudamericano.tesis.pedidosapp.Cart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sudamericano.tesis.pedidosapp.Model.Producto;

import java.util.ArrayList;
import java.util.List;


public class CartHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = "CartHelper";

    // Database Version
    private static final int DATABASE_VERSION = 26;

    // Database Name
    private static final String DATABASE_NAME = "sistemadepedidosManager";

    // Table Names
    private static final String TABLE_CART_HEADER = "cart";
    private static final String TABLE_CART_DETAILS = "cart_details";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_CART_ID = "cart_id";
    private static final String KEY_PRODUCTO_ID = "product_id";
    private static final String KEY_PRODUCTO_NAME = "product_name";
    private static final String KEY_PRODUCTO_URL_IMAGE = "product_url_image";
    private static final String KEY_PRODUCTO_STOCK = "product_max_stock";
    private static final String KEY_COUPON_CODE = "coupon_code";

    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_UNIT_PRICE = "unit_price";
    private static final String KEY_DISCOUNT_TOTAL = "discount";
    private static final String KEY_DISCOUNT_TYPE = "discount_type";

    private static final String KEY_TAXES_PERCENTAGE = "taxes_percent";
    private static final String KEY_TAXABLE = "taxable";
    private static final String KEY_SUBTOTAL = "subtotal";
    private static final String KEY_SUBTOTAL_TAXES = "subtotal_taxes";

    private static final String KEY_SUBTOTAL_WITH_TAXES = "subtotal_with_taxes";
    private static final String KEY_SUBTOTAL_WITH_TAXES0 = "subtotal_with_taxes0";

    public static final String DISCOUNT_PERCENT = "percent";
    public static final String DISCOUNT_FIXED_CART = "fixed_cart";
    public static final String IS_TAXABLE = "taxable";
    public static final String NOT_TAXABLE = "none";


// Default values

    private static final int DEFAULT_VALUE_CART_ID = 1;


    private static final java.lang.String CREATE_TABLE_CART_HEADER = "CREATE TABLE "
            + TABLE_CART_HEADER + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_CUSTOMER_ID + " INTEGER , "
            + KEY_DISCOUNT_TYPE + " TEXT, "
            + KEY_COUPON_CODE + " TEXT, "
            + KEY_TAXES_PERCENTAGE + " REAL, "
            + KEY_DISCOUNT_TOTAL + " REAL, "
            + KEY_SUBTOTAL_WITH_TAXES + " REAL, "
            + KEY_SUBTOTAL_WITH_TAXES0 + " REAL, "
            + KEY_TOTAL + " REAL, "
            + KEY_CREATED_AT + " DATETIME" + " DEFAULT CURRENT_TIMESTAMP);";

    private static final java.lang.String CREATE_TABLE_CART_DETAIL = "CREATE TABLE "
            + TABLE_CART_DETAILS + " ("
            + KEY_CART_ID + " INTEGER, "
            + KEY_PRODUCTO_ID + " INTEGER PRIMARY KEY, "
            + KEY_PRODUCTO_NAME + " TEXT , "
            + KEY_PRODUCTO_URL_IMAGE + " TEXT , "
            + KEY_PRODUCTO_STOCK + " REAL , "

            + KEY_QUANTITY + " INTEGER, "
            + KEY_UNIT_PRICE + " REAL, "
            + KEY_SUBTOTAL + " REAL, "
            + KEY_DISCOUNT_TOTAL + " REAL, "
            + KEY_SUBTOTAL_TAXES + " REAL, "

            + KEY_DISCOUNT_TYPE + " TEXT, "
            + KEY_TAXABLE + " INTEGER, "


            + KEY_CREATED_AT + " DATETIME" + " DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + KEY_CART_ID + ") REFERENCES " + TABLE_CART_HEADER + "(" + KEY_ID + ") "
            + ");";


    public CartHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART_HEADER);
        db.execSQL(CREATE_TABLE_CART_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_HEADER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_DETAILS);
        // create new tables
        onCreate(db);
    }


    /**
     * Insert Produt to Cart
     *
     * @param product
     * @param quantity
     * @return boolean
     */
    public boolean insertProduct(Producto product, int quantity) {

        long cart_id = getorCreateHeaderId();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CART_ID, cart_id);
        values.put(KEY_PRODUCTO_ID, product.getmId());
        values.put(KEY_PRODUCTO_NAME, product.getmTitle());
        values.put(KEY_PRODUCTO_URL_IMAGE, product.getmUrlImage());
        values.put(KEY_PRODUCTO_STOCK, product.getmStock_quantity());
        values.put(KEY_UNIT_PRICE, product.getmPrice());

        if (product.ismIsTaxable()) {
            values.put(KEY_TAXABLE, 1);
        } else {
            values.put(KEY_TAXABLE, 0);
        }


        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_SUBTOTAL, product.getmPrice() * quantity);

        try {
            db.insertOrThrow(TABLE_CART_DETAILS, null, values);

            updateDiscounttoAllProductsCart();
            updateTaxes();

            return true;
        } catch (Exception ex) {
            Log.e("ex",ex.toString());
            Log.e("product", "cant insert product");

            return false;
        }

    }

    /**
     * Update Product Quantity
     *
     * @param product_id
     * @param quantity
     * @return
     */
    public boolean updateQuantityProduct(int product_id, double unit_price, int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_UNIT_PRICE, unit_price);
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_SUBTOTAL, unit_price * quantity);

        int update = db.update(TABLE_CART_DETAILS, values, KEY_PRODUCTO_ID + " = ?",
                new String[]{String.valueOf(product_id)});

        if (update > 0) {
            updateDiscounttoAllProductsCart();
            updateTaxes();
            return true;
        } else {
            Log.e("product", "cant update product");
            return false;
        }
    }


    public void deleteCart(){
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_CART_DETAILS, KEY_CART_ID + " = ?",
                new String[]{String.valueOf(DEFAULT_VALUE_CART_ID)});
         delete = db.delete(TABLE_CART_HEADER, KEY_ID + " = ?",
                new String[]{String.valueOf(DEFAULT_VALUE_CART_ID)});

    }

    public long getorCreateHeaderId() {
        Cart cart = this.getCartHeader();
        if (cart != null) {
            return cart.getmCartID();
        } else {
            long header = this.createHeader();
            return header;
        }

    }


    /**
     * get single Cart
     */
    public Cart getCartHeader() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CART_HEADER + " WHERE "
                + KEY_ID + " = " + DEFAULT_VALUE_CART_ID;
        // Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            Cart cart = new Cart();
            cart.setmCartID(c.getInt(c.getColumnIndex(KEY_ID)));
            cart.setmCustomerId(c.getInt(c.getColumnIndex(KEY_CUSTOMER_ID)));
            cart.setmDiscountType(c.getString(c.getColumnIndex(KEY_DISCOUNT_TYPE)));
            cart.setmCouponCode(c.getString(c.getColumnIndex(KEY_COUPON_CODE)));
            cart.setmDiscount(roundValue(c.getDouble(c.getColumnIndex(KEY_DISCOUNT_TOTAL))));
            cart.setmTaxesPercentage(roundValue(c.getDouble(c.getColumnIndex(KEY_TAXES_PERCENTAGE))));
            cart.setmSubtotalWithTaxes0(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL_WITH_TAXES0))));
            cart.setmSubtotalWithTaxes(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL_WITH_TAXES))));
            cart.setmTotal(roundValue(c.getDouble(c.getColumnIndex(KEY_TOTAL))));
            return cart;

        } else {
            Log.e("cart", "cant obtain cart header");
            return null;
        }
    }

    /**
     * Create a Header with default values
     *
     * @return
     */
    public long createHeader() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, DEFAULT_VALUE_CART_ID);
        values.put(KEY_CUSTOMER_ID, 0);
        values.put(KEY_DISCOUNT_TYPE, "");
        values.put(KEY_TAXES_PERCENTAGE, 0);
        values.put(KEY_SUBTOTAL_WITH_TAXES, 0);
        values.put(KEY_SUBTOTAL_WITH_TAXES0, 0);
        values.put(KEY_DISCOUNT_TOTAL, 0);



        values.put(KEY_TOTAL, 0);


        // insert row
        try {
            long l = db.insertOrThrow(TABLE_CART_HEADER, null, values);
            return l;
        } catch (Exception ex) {
            Log.e(LOG, ex.getMessage());

            return 0;
        }

    }

    /**
     * Updating a Total Header Values
     */
    public boolean updateHeader_(double subtotal_with_taxes, double subtotal_with_taxes0) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SUBTOTAL_WITH_TAXES0, subtotal_with_taxes0);
        values.put(KEY_SUBTOTAL_WITH_TAXES, subtotal_with_taxes);
        values.put(KEY_TOTAL, subtotal_with_taxes + subtotal_with_taxes0);

        int b = db.update(TABLE_CART_HEADER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(DEFAULT_VALUE_CART_ID)});
        if (b > 0) {
            return true;
        } else {
            Log.e("cart", "cant update cart header");
            return false;
        }
    }


    /**
     * get single CartDetails
     */
    public CartDetails getProduct(long product_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CART_DETAILS + " WHERE "
                + KEY_PRODUCTO_ID + " = " + product_id;
        //  Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {

            CartDetails cartDetails = new CartDetails();

            cartDetails.setmProductoId(c.getInt(c.getColumnIndex(KEY_PRODUCTO_ID)));
            cartDetails.setmName(c.getString(c.getColumnIndex(KEY_PRODUCTO_NAME)));
            cartDetails.setmImageUrl(c.getString(c.getColumnIndex(KEY_PRODUCTO_URL_IMAGE)));
            cartDetails.setmStockQuantity(c.getInt(c.getColumnIndex(KEY_PRODUCTO_STOCK)));
            cartDetails.setmQuantity(c.getInt(c.getColumnIndex(KEY_QUANTITY)));
            cartDetails.setmUnitPrice(roundValue(c.getDouble(c.getColumnIndex(KEY_UNIT_PRICE))));
            cartDetails.setmDiscount(roundValue(c.getDouble(c.getColumnIndex(KEY_DISCOUNT_TOTAL))));
            cartDetails.setmDiscountType(c.getString(c.getColumnIndex(KEY_DISCOUNT_TYPE)));

            int taxableInt = c.getInt(c.getColumnIndex(KEY_TAXABLE));

            if (taxableInt == 1) {
                cartDetails.setmIsTaxable(true);
            } else {
                cartDetails.setmIsTaxable(false);
            }

            cartDetails.setmTaxesTotal(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL_TAXES))));
            cartDetails.setmSubtotal(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL))));

            return cartDetails;

        } else {
            return null;
        }
    }


    public double roundValue(double value) {
        return Math.round(value * 100.0) / 100.0;

    }

    /**
     * getting all Cart
     */
    public List<CartDetails> getCart() {
        List<CartDetails> todos = new ArrayList<CartDetails>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART_DETAILS;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                CartDetails cartDetails = new CartDetails();

                cartDetails.setmProductoId(c.getInt(c.getColumnIndex(KEY_PRODUCTO_ID)));
                cartDetails.setmName(c.getString(c.getColumnIndex(KEY_PRODUCTO_NAME)));
                cartDetails.setmImageUrl(c.getString(c.getColumnIndex(KEY_PRODUCTO_URL_IMAGE)));
                cartDetails.setmStockQuantity(c.getInt(c.getColumnIndex(KEY_PRODUCTO_STOCK)));
                cartDetails.setmQuantity(c.getInt(c.getColumnIndex(KEY_QUANTITY)));
                cartDetails.setmUnitPrice(roundValue(c.getDouble(c.getColumnIndex(KEY_UNIT_PRICE))));
                cartDetails.setmDiscount(roundValue(c.getDouble(c.getColumnIndex(KEY_DISCOUNT_TOTAL))));
                cartDetails.setmDiscountType(c.getString(c.getColumnIndex(KEY_DISCOUNT_TYPE)));

                int taxableInt = c.getInt(c.getColumnIndex(KEY_TAXABLE));

                if (taxableInt == 1) {
                    cartDetails.setmIsTaxable(true);
                } else {
                    cartDetails.setmIsTaxable(false);
                }

                cartDetails.setmTaxesTotal(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL_TAXES))));
                cartDetails.setmSubtotal(roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL))));

                todos.add(cartDetails);
            } while (c.moveToNext());
        }

        return todos;
    }


    /**
     * getting Cart Intems Count
     */
    public int getCartCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CART_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }


    /**
     * getting Cart Intems Count
     */
    public double getCartTotal() {
        String countQuery = "SELECT  * FROM " + TABLE_CART_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(countQuery, null);
        double subtotal = 0;
        if (c.moveToFirst()) {
            do {
                subtotal += roundValue(c.getDouble(c.getColumnIndex(KEY_SUBTOTAL)));
            } while (c.moveToNext());
        }

        return subtotal;

    }


    /**
     * Add Percentaje Taxes to Cart
     *
     * @param value
     * @return
     */
    public boolean addTaxesPercentajetoCart(double value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TAXES_PERCENTAGE, value);
        int update = db.update(TABLE_CART_HEADER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(DEFAULT_VALUE_CART_ID)});

        if (update > 0) {
            updateTaxes();
            return true;
        } else {
            Log.e("taxes", "cant add taxes percentage to cart header");
            return false;
        }
    }


    /**
     * Add Discount Type and Value to Cart
     *
     * @param discount_type
     * @param value
     * @param coupon_code
     * @return
     */
    public boolean addDiscountCart(String discount_type, double value, String coupon_code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DISCOUNT_TOTAL, value);
        values.put(KEY_DISCOUNT_TYPE, discount_type);
        values.put(KEY_COUPON_CODE, coupon_code);


        int update = db.update(TABLE_CART_HEADER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(DEFAULT_VALUE_CART_ID)});

        if (update > 0) {
            updateDiscounttoAllProductsCart();
            return true;
        } else {
            Log.e("taxes", "cant add discount to cart header");
            return false;
        }
    }


    public boolean addDiscountProduct(int product_id, String discount_type, double value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DISCOUNT_TOTAL, value);
        values.put(KEY_DISCOUNT_TYPE, discount_type);

        int update = db.update(TABLE_CART_DETAILS, values, KEY_PRODUCTO_ID + " = ?",
                new String[]{String.valueOf(product_id)});

        if (update > 0) {
            updateTaxes();
            return true;
        } else {
            Log.d("No Discount Added", "No se pudo agregar un descuento" + product_id);
            return false;
        }
    }


    public void RemoveDiscountCart() {

        addDiscountCart("", 0, "");


        List<CartDetails> cart = getCart();
        for (CartDetails c : cart
                ) {
            addDiscountProduct(c.getmProductoId(), "", 0);
        }
        updateTaxes();
    }

    private void updateDiscounttoAllProductsCart() {
        Cart cartHeader = getCartHeader();

        String discountType = cartHeader.getmDiscountType();
        double discountValue = cartHeader.getmDiscount();

        List<CartDetails> cart = getCart();


        double total_discount = 0;
        double cart_count = getCartCount();

        for (CartDetails c : cart
                ) {

            double subtotal = c.getmSubtotal();

            if (discountType.equals(DISCOUNT_PERCENT) && discountValue > 0) {
                total_discount = subtotal * discountValue / 100;
            } else if (discountType.equals(DISCOUNT_FIXED_CART) && discountValue > 0) {
                total_discount =  (roundValue(discountValue / cart_count));
            } else {
                discountType = "";
            }

            addDiscountProduct(c.getmProductoId(), discountType, total_discount);

        }


    }

    /**
     * Update Total Taxes in a Single Product
     *
     * @param product_id
     * @param value
     * @return
     */
    public boolean updateTaxesValueProduct(int product_id, double value) {


        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_SUBTOTAL_TAXES, value);

        int update = db.update(TABLE_CART_DETAILS, values, KEY_PRODUCTO_ID + " = ?",
                new String[]{String.valueOf(product_id)});


        if (update > 0) {
            return true;
        } else {
            Log.e("taxes", "cant update taxes");
            return false;
        }
    }


    /**
     * Update Taxes from the Cart Details
     */
    private void updateTaxes() {

        List<CartDetails> cart = getCart();
        Cart cartHeader = getCartHeader();

        double TaxesPercentage = cartHeader.getmTaxesPercentage();

        for (CartDetails c : cart
                ) {

            if (c.ismIsTaxable() && TaxesPercentage > 0) {
                double taxes = (c.getmSubtotalWithDiscount() * TaxesPercentage / 100);
                updateTaxesValueProduct(c.getmProductoId(), taxes);

            }

        }
        updateHeaderTotals();
    }

    /**
     * Deleting a Product
     */
    public boolean deleteProduct(long product_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_CART_DETAILS, KEY_PRODUCTO_ID + " = ?",
                new String[]{String.valueOf(product_id)});

        //  Log.e(LOG, "Eliminando el producto: " + product_id);


        if (delete >= 1) {
            updateTaxes();
            return true;
        } else {
            Log.e("product", "cant delete product");
            return false;
        }

    }


    private void updateHeaderTotals() {

        List<CartDetails> cart = getCart();
        double subtotal_with_taxes = 0;
        double subtotal_with_taxes0 = 0;

        for (CartDetails c : cart
                ) {


            if (c.ismIsTaxable()) {
                subtotal_with_taxes = subtotal_with_taxes + c.getmTotal();
            } else {
                subtotal_with_taxes0 = subtotal_with_taxes0 + c.getmTotal();
            }

        }

        updateHeader_(subtotal_with_taxes, subtotal_with_taxes0);


    }


}
