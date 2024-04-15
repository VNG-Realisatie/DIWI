import { createContext, PropsWithChildren, useState } from "react";

type LoadingContextType = {
    loading: boolean;
    setLoading: (isLoading: boolean) => void;
};

const LoadingContext = createContext<LoadingContextType>({
    loading: false,
    setLoading: (isLoading: boolean) => {},
});

export const LoadingProvider = ({ children }: PropsWithChildren) => {
    const [loading, setLoading] = useState(false);

    return <LoadingContext.Provider value={{ loading, setLoading }}>{children}</LoadingContext.Provider>;
};

export default LoadingContext;
