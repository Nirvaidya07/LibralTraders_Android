# Getting Started Place Picker

# Start Activity for Result

 val intent = Intent(this@AddEditAddressActivity, AddressPickerActivity::class.java)
                    intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL, 16.0f)
                    startActivityForResult(intent, RC_PLACE_PICKER)


# onActivityResult


           if (requestCode == RC_PLACE_PICKER) {
                val address: Address? = data?.getParcelableExtra(RESULT_ADDRESS) as Address
            }
    
    
![MediaPipe](images/place_small.png?raw=true "PLACE PICKER")