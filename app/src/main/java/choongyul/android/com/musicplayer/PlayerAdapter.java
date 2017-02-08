package choongyul.android.com.musicplayer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myPC on 2017-02-02.
 */

public class PlayerAdapter extends PagerAdapter{
    List<Music> datas;
    Context context;
    LayoutInflater inflater;

    public PlayerAdapter(Context context) {
        this.datas = DataLoader.getDatas(context);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    // 데이터 총 개수
    @Override
    public int getCount() {
        return datas.size();
    }

    // instantiateItem에서 리턴된 Object가 view가 맞는지 확인하는 함수.
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // listView의 getView와 같은 역할이다.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.player_card_item , null);
        ImageView cardImageView = (ImageView) view.findViewById(R.id.cardimgView);
        TextView cardTxtTitle = (TextView) view.findViewById(R.id.cardTxtTitle);
        TextView cardTxtArtist = (TextView) view.findViewById(R.id.cardTxtArtist);

        //실제 음악데이터 가져오기
        Music music = datas.get(position);
        cardTxtTitle.setText(music.getTitle());
        cardTxtArtist.setText(music.getArtist());

        Glide.with(context).load(music.album_image).placeholder(android.R.drawable.ic_menu_close_clear_cancel).into(cardImageView);

        // 생성한 뷰를 컨테이너에 담아준다. 컨테이너 = 뷰페이져를 생성한 최외곽 레이아웃 개념
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView( (View) object);
    }
}
