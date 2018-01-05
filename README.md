# JDAddressSelector
级联地址选择器。
![image](https://github.com/chihane/JDAddressSelector/raw/master/screenshots/aa4.jpg)

## 添加依赖

项目的 `build.gradle` 中：

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io"}
        }
    }

    
## 使用方法
        1
        AddressSelector selector = new AddressSelector(this, null, null, null, null, null, null);
        selector.setOnAddressSelectedListener(this);


        2
        buttonBottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BottomDialog.show(MainActivity.this, MainActivity.this);
                BottomDialog dialog = new BottomDialog(MainActivity.this, "江苏省", "苏州市", "吴中区", "越溪街道", "越溪管理区", "木林社区");
                dialog.setOnAddressSelectedListener(MainActivity.this);
                dialog.show();
            }
        });
    @Override
    public void onAddressSelected(String address, String id) {
        T.showShort(this, address + "---->" + id);
    }
    
## 关于我
--

## 许可证
--