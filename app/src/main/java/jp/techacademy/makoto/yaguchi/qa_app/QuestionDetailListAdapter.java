package jp.techacademy.makoto.yaguchi.qa_app;

/**
 * Created by Makoto Yaguchi on 2017/12/23.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailListAdapter extends BaseAdapter {
    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Question mQustion;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFavouriteRef;

    public QuestionDetailListAdapter(Context context, Question question) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQustion = question;
    }

    @Override
    public int getCount() {
        return 1 + mQustion.getAnswers().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return mQustion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }
            String body = mQustion.getBody();
            String name = mQustion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            //お気に入りボタンの追加
            final Button favouriteButton = (Button) convertView.findViewById(R.id.favouriteButton);
            final Button unFavouriteButton = (Button) convertView.findViewById(R.id.unFavouriteButton);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String favourite = mQustion.getFavourite();

            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFavouriteRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mQustion.getGenre())).child(mQustion.getQuestionUid());

            //ログイン状態によるボタン表示の切り替え
            if (user == null) {
                favouriteButton.setVisibility(View.INVISIBLE);
                unFavouriteButton.setVisibility(View.INVISIBLE);
            } else if (favourite.equals("0")) {
                favouriteButton.setVisibility(View.GONE);
                unFavouriteButton.setVisibility(View.VISIBLE);
            } else {
                favouriteButton.setVisibility(View.VISIBLE);
                unFavouriteButton.setVisibility(View.GONE);

            }

            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> favourite = new HashMap<>();
                    favourite.put("favourite", "0");
                    mFavouriteRef.updateChildren(favourite);
                    mQustion.setFavourite("0");
                    favouriteButton.setVisibility(View.GONE);
                    unFavouriteButton.setVisibility(View.VISIBLE);
                }
            });

            unFavouriteButton.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> favourite = new HashMap<>();
                    favourite.put("favourite", "1");
                    mFavouriteRef.updateChildren(favourite);
                    favouriteButton.setVisibility(View.VISIBLE);
                    unFavouriteButton.setVisibility(View.GONE);
                }
            }));


            byte[] bytes = mQustion.getImageBytes();
            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mQustion.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }

        return convertView;
    }
}