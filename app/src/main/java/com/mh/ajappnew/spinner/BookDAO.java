package com.mh.ajappnew.spinner;

import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> list() {
        List<Book> bookList = new ArrayList<Book>();

        String[] names = {"愤怒的小鸟", "汤姆猫", "落汤鸡", "牛牛", "哈巴狗", "神龙", "烤鸭",
                "小象", "美人鱼", "九尾狐","愤怒的小鸟", "汤姆猫", "落汤鸡", "牛牛", "哈巴狗", "神龙", "烤鸭",
                "小象", "美人鱼", "九尾狐","愤怒的小鸟", "汤姆猫", "落汤鸡", "牛牛", "哈巴狗", "神龙", "烤鸭",
                "小象", "美人鱼", "九尾狐"};
//        int[] images = {R.drawable.bird, R.drawable.cat, R.drawable.chicken,
//                R.drawable.cow, R.drawable.dog, R.drawable.dragon,
//                R.drawable.duck, R.drawable.elephant, R.drawable.fish,
//                R.drawable.fox};

        Book b = null;
        for (int i = 0; i < names.length; i++) {
            b = new Book();
            b.setId(i + 1);
            b.setName(names[i]);
            //b.setImage(images[i]);

            bookList.add(b);
        }

        return bookList;
    }
}
