package com.Patane.util.general;

import com.Patane.util.general.Messenger.Msg;

public class ErrorHandler {
	public static <T> T optionalLoadError(Msg msgType, Importance importance, String error) throws LoadException{
		switch(importance){
		case NONE:
			break;
		case DEBUG:
			Messenger.debug(msgType, error);
			break;
		case MINOR:
			Messenger.send(msgType, error);
			break;
		case REQUIRED:
			throw new LoadException(error);
		}
		return null;
	}
	public static class LoadException extends Exception{
		private static final long serialVersionUID = -3716749242596637784L;

		public LoadException(String message){
			super(message);
		}
	}
	public static class YMLException extends Exception{
		private static final long serialVersionUID = -2955721517942223019L;

		public YMLException(String message){
			super(message);
		}
	}
	public static enum Importance {
		NONE{}, DEBUG{}, MINOR{}, REQUIRED{};
	}
}
