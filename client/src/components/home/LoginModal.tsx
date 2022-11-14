import styled from 'styled-components';
import { FcGoogle } from 'react-icons/fc';

type LoginModalType = {
	onClick: () => void;
};

function LoginModal({ onClick }: LoginModalType) {
	return (
		<LoginModalStyle>
			<WhiteBox>
				<H2>로그인</H2>
				<GoogleLogin>
					<FcGoogle className="google-icon" /> 구글로 로그인하기
				</GoogleLogin>
			</WhiteBox>
			<Backdrop
				onClick={(e) => {
					e.preventDefault();

					onClick();
				}}
			/>
		</LoginModalStyle>
	);
}

export default LoginModal;

const LoginModalStyle = styled.div`
	width: 100vw;
	height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	position: fixed;
	top: 0;
`;

const WhiteBox = styled.div`
	width: 500px;
	height: 300px;
	padding: 47px 70px;
	display: flex;
	flex-direction: column;
	align-items: center;
	box-shadow: 0 0 30px rgba(30, 30, 30, 0.185);
	background-color: var(--background-color);
	border-radius: var(--radius);
	z-index: 9999;

	// Tablet
	@media screen and (max-width: 980px) {
		width: 400px;
		padding: 47px 50px;
	}
	// Mobile
	@media screen and (max-width: 640px) {
		width: 300px;
		height: 250px;
		padding: 37px 30px;
	}
`;

const H2 = styled.div`
	font-size: var(--x-large);
	font-weight: 700;
	margin-bottom: 47px;

	// Mobile
	@media screen and (max-width: 640px) {
		margin-bottom: 37px;
		font-size: 24px;
	}
`;

const GoogleLogin = styled.button`
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20px 85px;
	width: 100%;
	background-color: var(--white);
	border: 1px solid var(--gray-500);
	border-radius: var(--radius);
	font-size: 18px;
	transition: 0.2s;

	&:hover {
		background-color: var(--gray-50);
	}

	.google-icon {
		font-size: 30px;
	}

	// Tablet
	@media screen and (max-width: 980px) {
		padding: 20px 55px;
	}
	// Mobile
	@media screen and (max-width: 640px) {
		padding: 15px 35px;
		font-size: var(--midium);
	}
`;

const Backdrop = styled.div`
	width: 100vw;
	height: 100vh;
	position: fixed;
	top: 0;
	background-color: rgba(0, 0, 0, 0.4);
	z-index: 8888;
`;
