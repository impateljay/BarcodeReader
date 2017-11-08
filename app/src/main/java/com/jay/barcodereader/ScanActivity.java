package com.jay.barcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
    }

    @Override
    public void onScanned(Barcode barcode) {
        Log.e("BARCODE_","onScanned");

        // playing barcode reader beep sound
        barcodeReader.playBeep();

        // ticket details activity by passing barcode
//        Intent intent = new Intent(ScanActivity.this, TicketActivity.class);
//        intent.putExtra("code", barcode.displayValue);
//        startActivity(intent);
//        Toast.makeText(getApplicationContext(),barcode.displayValue,Toast.LENGTH_LONG).show();
        Log.e("BARCODE_", String.valueOf(barcode.format));
        Log.e("BARCODE_",barcode.displayValue);
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {
        Log.e("BARCODE_","onScannedMultiple");

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        Log.e("BARCODE_","onBitmapScanned");

    }

    @Override
    public void onScanError(String s) {
        Log.e("BARCODE_","onScanError");
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }
}
