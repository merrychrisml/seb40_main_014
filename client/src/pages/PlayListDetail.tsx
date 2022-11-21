import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import styled from 'styled-components';
import { getPlayList } from '../api/playlistApi';
import MusicList, { music } from '../components/playlist/MusicList';
import PlayListInfo from '../components/playlist/PlayListInfo';
import { musicInfoType } from './MakePlayList';

export type plinfo = {
	memberId: string;
	title: string;
	playlist: Array<musicInfoType>;
	categoryList: Array<string>;
	public: boolean;
	like?: number;
};

export type PlayListInfoProps = {
	playListInfo?: plinfo;
	plList?: Array<musicInfoType>;
	setPlList?: Dispatch<SetStateAction<Array<musicInfoType>>>;
};

const PlayListDetail = () => {
	const [playListInfo, setPlayListInfo] = useState();

	useEffect(() => {
		getPlayList().then((res) => {
			if (res.code) {
				alert(res);
			} else {
				setPlayListInfo(res);
			}
		});
	}, []);

	const props: PlayListInfoProps = {
		playListInfo,
	};
	return (
		<PlayListDetailStyle>
			{playListInfo && (
				<>
					<PlayListInfo {...props} />
					<MusicList {...props} />
				</>
			)}
		</PlayListDetailStyle>
	);
};

export default PlayListDetail;

const PlayListDetailStyle = styled.div`
	display: flex;
	flex-direction: column;
`;
