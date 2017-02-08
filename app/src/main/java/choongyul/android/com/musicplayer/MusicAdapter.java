package choongyul.android.com.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myPC on 2017-02-01.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder>{
    List<Music> datas;
    Context context;
    Intent intent = null;

    public MusicAdapter(Context context) {
        this.datas = DataLoader.getDatas(context);
        this.context = context;
        intent = new Intent(context, PlayerActivity.class);
    }

    @Override
    public MusicAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 이전에는 context 자리에 parent.getContext() 가 들어갔는데 우리는 생성자에 context를 받아왔기 때문에 바로 사용이 가능하다.
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }



    // 여기서 new를 해주면 성능이 아주 안좋아진다.
    @Override
    public void onBindViewHolder(MusicAdapter.Holder holder, int position) {
        // 데이터를 행 단위로 꺼낸다
        final Music music = datas.get(position);

        // 홀더에 데이터를 세팅한다.
        holder.txtTitle.setText(music.getTitle());
        holder.txtArtist.setText(music.getArtist());

        holder.position = position;

        Glide.with(context)
                .load(music.album_image) //로드할 대상 Uri
                .placeholder(android.R.drawable.ic_menu_close_clear_cancel) // 이미지가 없으면 대신할 이미지 선택.
                .into(holder.imageView); // 세팅할 이미지뷰

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.cardView.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle,txtArtist;
        CardView cardView;

        int position;


        public Holder(View v) {
            super(v);

            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtArtist = (TextView) v.findViewById(R.id.txtArtist);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            cardView = (CardView) v.findViewById(R.id.cardView);
            // 클릭 되었을 때 클릭된 position을 받아서 뷰페이저로 넘어감`
            cardView.setOnClickListener(listener);
        }
        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        };
    }
}
