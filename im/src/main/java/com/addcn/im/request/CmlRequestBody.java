/**
 * Copyright (c) 2019 addcn
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
package com.addcn.im.request;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Author: WangYongQi
 */

public abstract class CmlRequestBody extends RequestBody {

    private RequestBody requestBody;
    private BufferedSink bufferedSink;

    public CmlRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public MediaType contentType() {
        if (requestBody != null) {
            return requestBody.contentType();
        } else {
            return null;
        }
    }

    @Override
    public long contentLength() throws IOException {
        if (requestBody != null) {
            return requestBody.contentLength();
        } else {
            return 0;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        if (requestBody != null) {
            requestBody.writeTo(bufferedSink);
        }
        if (bufferedSink != null) {
            // 必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();
        }
    }

    private Sink sink(Sink sink) {

        return new ForwardingSink(sink) {
            private long total;
            private long current;
            private int last = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (total == 0) {
                    total = contentLength();
                }
                current += byteCount;
                int now = (int) (current * 100 / total);
                if (last < now) {
                    loading(last, 100, total == current);
                    last = now;
                }
            }
        };
    }

    public abstract void loading(long current, long total, boolean done);

}