/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniusgithub.mediaplayer.player;

import com.geniusgithub.mediaplayer.upnp.MediaItem;

public interface PlayerEngineListener {
	
	public void onTrackPlay(MediaItem itemInfo); 
	public void onTrackStop(MediaItem itemInfo);
	public void onTrackPause(MediaItem itemInfo);
	public void onTrackPrepareSync(MediaItem itemInfo);
	public void onTrackPrepareComplete(MediaItem itemInfo);
	public void onTrackStreamError(MediaItem itemInfo);
	public void onTrackPlayComplete(MediaItem itemInfo);
}
